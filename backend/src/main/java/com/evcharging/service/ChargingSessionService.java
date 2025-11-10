package com.evcharging.service;

import com.evcharging.dto.ChargingSessionDTO;
import com.evcharging.entity.ChargingPoint;
import com.evcharging.entity.ChargingSession;
import com.evcharging.entity.Reservation;
import com.evcharging.entity.Transaction;
import com.evcharging.enums.*;
import com.evcharging.repository.*;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

@Slf4j
@Service
public class ChargingSessionService {
    @Autowired
    private ApplicationContext context;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private WalletService walletService;
    @Autowired
    private PaymentGatewayService paymentGatewayService;
    @Autowired
    private InvoiceService invoiceService;
    @Autowired
    private InvoiceRepository invoiceRepository;

    private final ChargingSessionRepository sessionRepo;
    private final ReservationRepository reservationRepo;
    private final TransactionRepository transactionRepo;
    private final NotificationService notificationService;
    private final ChargingPointRepository chargingPointRepo;
    private final PricingService pricingService;


    public ChargingSessionService(ChargingSessionRepository sessionRepo,
                                  ReservationRepository reservationRepo,
                                  TransactionRepository transactionRepo,
                                  NotificationService notificationService,
                                  ChargingPointRepository chargingPointRepo,
                                  PricingService pricingService) {
        this.sessionRepo = sessionRepo;
        this.reservationRepo = reservationRepo;
        this.transactionRepo = transactionRepo;
        this.notificationService = notificationService;
        this.chargingPointRepo = chargingPointRepo;
        this.pricingService = pricingService;
    }

    // Bắt đầu phiên sạc từ một Reservation hợp lệ
    @Transactional
    public ChargingSessionDTO startSession(Long reservationId, int startSoc) {
        Reservation reservation = reservationRepo.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));

        if (reservation.getStatus() != ReservationStatus.CONFIRMED) {
            throw new IllegalStateException("Reservation not valid for charging");
        }

        // Đánh dấu Reservation đã được sử dụng
        reservation.setStatus(ReservationStatus.COMPLETED);

        ChargingSession session = new ChargingSession();
        session.setReservation(reservation);
        session.setDriver(reservation.getDriver());
        session.setStation(reservation.getStation());
        session.setStartTime(OffsetDateTime.now());
        session.setStartSoc(startSoc);
        session.setEndSoc(startSoc);
        session.setStatus(SessionStatus.CHARGING);
        session.setChargingPoint(reservation.getChargingPoint());

        session = sessionRepo.save(session);

        return toDTO(session); // trả về DTO
    }

    // Kết thúc phiên sạc thủ công, cập nhật thông tin và tạo Transaction
    @Transactional
    public ChargingSessionDTO endSession(Long sessionId, PaymentMethod method) {
        ChargingSession session = sessionRepo.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));

        if (session.getStatus() != SessionStatus.CHARGING) {
            throw new IllegalStateException("Session is not active");
        }

        //  Lấy dữ kiện từ session
        int endSoc = session.getEndSoc() != null ? session.getEndSoc() : 100;
        double energy = session.getEnergyConsumed();

        // Cập nhật session
        session.setEndTime(OffsetDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
        session.setEndSoc(endSoc);
        session.setEnergyConsumed(energy);
        session.setStatus(SessionStatus.COMPLETED);

        // Tính phí
        BigDecimal finalCost = pricingService.calculateChargingFee(session);
        session.setCost(finalCost.doubleValue());

        // Giải phóng trụ
        ChargingPoint point = session.getChargingPoint();
        point.setStatus(ChargingPointStatus.AVAILABLE);
        chargingPointRepo.save(point);

        // Gửi thông báo
        notificationService.sendChargingComplete(session.getDriver());

        // Tạo transaction
        Transaction tx = transactionService.createTransaction(session, finalCost, TransactionStatus.PENDING);
        session.setTransaction(tx);

        // Xử lý thanh toán
        String paymentUrl = null;
        if (method != null) {
            tx.setPaymentMethod(method);

            if (method == PaymentMethod.EWALLET) {
                walletService.deductBalance(session.getDriver().getId(), finalCost.doubleValue(),
                        "Thanh toán phiên sạc #" + session.getId());
                tx.setStatus(TransactionStatus.SUCCESS);
                invoiceService.createInvoice(tx);
            } else if (method == PaymentMethod.BANKING) {
                tx.setStatus(TransactionStatus.PENDING);
                String returnUrl = "https://your-frontend.com/payment/result";
                paymentUrl = paymentGatewayService.redirectToGateway(tx, returnUrl);
            }
        }

        // Lưu session
        sessionRepo.save(session);

        // Trả DTO
        ChargingSessionDTO dto = toDTO(session);
        dto.setTransactionId(tx.getId());
        dto.setPaymentUrl(paymentUrl);
        return dto;
    }

    // Mapper entity -> DTO
    private ChargingSessionDTO toDTO(ChargingSession session) {
        Long transactionId = session.getTransaction() != null ? session.getTransaction().getId() : null;

        ChargingSessionDTO dto = new ChargingSessionDTO(
                session.getId(),
                session.getStation().getName(),
                session.getDriver().getFullName(),
                session.getReservation() != null && session.getReservation().getChargingPoint() != null
                        ? session.getReservation().getChargingPoint().getCode()
                        : null,
                session.getStartTime(),
                session.getEndTime(),
                session.getStartSoc(),
                session.getEndSoc(),
                session.getEnergyConsumed(),
                session.getCost(),
                session.getStatus(),
                transactionId,
                null // paymentUrl sẽ set sau nếu có
        );

        return dto;
    }

    public void checkAndAutoEnd(ChargingSession session, int currentSoc) {
        if (currentSoc == 100) {
            context.getBean(ChargingSessionService.class)
                    .endSession(session.getId(), session.getPaymentMethod());
        }
    }

    @Scheduled(fixedRate = 3000) // chạy mỗi 3 giây
    public void autoUpdateChargingSessions() {
        List<ChargingSession> activeSessions = sessionRepo.findByStatus(SessionStatus.CHARGING);

        for (ChargingSession session : activeSessions) {
            OffsetDateTime now = OffsetDateTime.now();
            OffsetDateTime lastUpdate = session.getLastUpdatedTime() != null
                    ? session.getLastUpdatedTime()
                    : session.getStartTime();

            // Tính thời gian trôi qua (h)
            double durationHours = Duration.between(lastUpdate, now).toMillis() / (1000.0 * 60 * 60);

            // Lấy công suất tối đa từ ChargingPoint
            ChargingPoint point = session.getChargingPoint();
            int maxPower = point != null && point.getMaxPower() != null ? point.getMaxPower() : 30; // fallback 30kW
            double power = maxPower * 0.6; // giả lập 60% công suất

            // Tính lượng điện đã nạp thêm
            double addedEnergy = power * durationHours;

            // Cập nhật energy
            double currentEnergy = session.getEnergyConsumed();
            double totalEnergy = currentEnergy + addedEnergy;
            session.setEnergyConsumed(totalEnergy);

            // Giả lập dung lượng pin xe (ví dụ 50 kWh)
            double batteryCapacity = 50.0;
            int startSoc = session.getStartSoc();
            int newSoc = Math.min(100, (int) (startSoc + (totalEnergy / batteryCapacity) * 100));
            session.setEndSoc(newSoc);

            //  Tính chi phí tạm tính
//            double energyFee = totalEnergy * point.getPricePerKwh();
//            long minutes = Duration.between(session.getStartTime(), now).toMinutes();
//            double timeFee = minutes * point.getPricePerMinute();

            // Nếu có reservation thì cộng thêm phí giữ chỗ
//            double reservationFee = 0;
//            if (session.getReservation() != null) {
//                Reservation reservation = session.getReservation();
//                long hours = Duration.between(reservation.getStartTime(), reservation.getExpireTime()).toHours();
//                reservationFee = hours * 10_000; // 10k VND mỗi giờ
//            }

            BigDecimal tempCost = pricingService.calculateChargingFee(session);
            session.setCost(tempCost.doubleValue());

            // Cập nhật thời gian
            session.setLastUpdatedTime(now);
            sessionRepo.save(session);

            // Gọi auto end nếu đủ điều kiện
            checkAndAutoEnd(session, newSoc);
        }
    }
}