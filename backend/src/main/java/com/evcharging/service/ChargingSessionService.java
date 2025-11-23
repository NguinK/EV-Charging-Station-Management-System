package com.evcharging.service;

import com.evcharging.dto.ChargingSessionDTO;
import com.evcharging.entity.*;
import com.evcharging.enums.*;
import com.evcharging.repository.*;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
    private EVDriverRepository evDriverRepository;

    private final ChargingSessionRepository sessionRepo;
    private final ReservationRepository reservationRepo;
    private final TransactionRepository transactionRepo;
    private final NotificationService notificationService;
    private final ChargingPointRepository chargingPointRepo;
    private final PricingService pricingService;
    private final SimpMessagingTemplate messagingTemplate;


    public ChargingSessionService(ChargingSessionRepository sessionRepo,
                                  ReservationRepository reservationRepo,
                                  TransactionRepository transactionRepo,
                                  NotificationService notificationService,
                                  ChargingPointRepository chargingPointRepo,
                                  PricingService pricingService,
                                  SimpMessagingTemplate messagingTemplate) {
        this.sessionRepo = sessionRepo;
        this.reservationRepo = reservationRepo;
        this.transactionRepo = transactionRepo;
        this.notificationService = notificationService;
        this.chargingPointRepo = chargingPointRepo;
        this.pricingService = pricingService;
        this.messagingTemplate = messagingTemplate;
    }

    // Bắt đầu phiên sạc từ một Reservation hợp lệ
    @Transactional
    public ChargingSessionDTO startSession(Long reservationId, int startSoc) {
        Reservation reservation = reservationRepo.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));

        if (reservation.getStatus() != ReservationStatus.CONFIRMED) {
            throw new IllegalStateException("Reservation not valid for charging");
        }

        if (startSoc < 0 || startSoc > 100) {
            throw new IllegalArgumentException("SOC must be between 0 and 100");
        }
        if (startSoc >= 95) {
            throw new IllegalArgumentException("Battery is nearly full, charging not recommended");
        }

        EVDriver driver = evDriverRepository.findById(reservation.getDriver().getId())
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        if (driver.getBatteryCapacity() == null || driver.getBatteryCapacity() <= 0) {
            throw new IllegalStateException("Driver has no valid battery capacity configured. Please update your vehicle information.");
        }
        if (driver.getConnectorType() == null) {
            throw new IllegalStateException("Driver has no connector type configured. Please update your vehicle information.");
        }

        // Đánh dấu Reservation đã được sử dụng
        reservation.setStatus(ReservationStatus.COMPLETED);

        ChargingSession session = new ChargingSession();
        session.setReservation(reservation);
        session.setDriver(driver);
//        session.setDriver(reservation.getDriver());
        session.setStation(reservation.getStation());
        session.setStartTime(OffsetDateTime.now());
        session.setStartSoc(startSoc);
        session.setEndSoc(startSoc);
        session.setStatus(SessionStatus.CHARGING);
        session.setChargingPoint(reservation.getChargingPoint());
        session.setEnergyConsumed(0.0);
        session.setCost(0.0);
        session = sessionRepo.save(session);
        ChargingSessionDTO dto = toDTO(session);

        // Push qua WebSocket cho client subscribe /topic/session/{id}
        messagingTemplate.convertAndSend("/topic/session/" + dto.getId(), dto);

        return dto;

    }

    @Transactional
    public ChargingSessionDTO startDirectSession(Long pointId, Long driverId, int startSoc) {
        ChargingPoint point = chargingPointRepo.findById(pointId)
                .orElseThrow(() -> new IllegalArgumentException("Charging point not found"));
        EVDriver driver = evDriverRepository.findById(driverId)
                .orElseThrow(() -> new IllegalArgumentException("Driver not found"));

        if (driver.getBatteryCapacity() == null || driver.getBatteryCapacity() <= 0) {
            throw new IllegalStateException("Driver has no valid battery capacity configured. Please update your vehicle information.");
        }

        ChargingSession session = new ChargingSession();
        session.setDriver(driver);
        session.setStation(point.getStation());
        session.setChargingPoint(point);
        session.setStartTime(OffsetDateTime.now());
        session.setStartSoc(startSoc);
        session.setEndSoc(startSoc);
        session.setStatus(SessionStatus.CHARGING);
        session.setEnergyConsumed(0.0);
        session.setCost(0.0);

        // Đánh dấu trụ đang bận
        point.setStatus(ChargingPointStatus.OCCUPIED);
        chargingPointRepo.save(point);

        session = sessionRepo.save(session);
        ChargingSessionDTO dto = toDTO(session);
        messagingTemplate.convertAndSend("/topic/session/" + dto.getId(), dto);

        return dto;
    }

    // Kết thúc phiên sạc thủ công, cập nhật thông tin và tạo Transaction
    @Transactional
    public ChargingSessionDTO endSession(Long sessionId) {
        ChargingSession session = sessionRepo.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));

        if (session.getStatus() != SessionStatus.CHARGING) {
            // đã kết thúc rồi thì trả DTO luôn, không tạo transaction nữa
            return toDTO(session);
        }

        int endSoc = session.getEndSoc() != null ? session.getEndSoc() : 100;
        double energy = session.getEnergyConsumed();

        session.setEndTime(OffsetDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
        session.setEndSoc(endSoc);
        session.setEnergyConsumed(energy);
        session.setStatus(SessionStatus.COMPLETED);

        BigDecimal finalCost = pricingService.calculateChargingFee(session);
        session.setCost(finalCost.doubleValue());

        ChargingPoint point = session.getChargingPoint();
        point.setStatus(ChargingPointStatus.AVAILABLE);
        chargingPointRepo.save(point);

        notificationService.sendChargingComplete(session.getDriver());

        // chỉ tạo transaction nếu chưa có
        if (session.getTransaction() == null) {
            Transaction tx = transactionService.createTransaction(session, finalCost, TransactionStatus.PENDING);
            session.setTransaction(tx);
        }

        sessionRepo.save(session);

        ChargingSessionDTO dto = toDTO(session);
        if (session.getTransaction() != null) {
            dto.setTransactionId(session.getTransaction().getId());
        }
        messagingTemplate.convertAndSend("/topic/session/" + dto.getId(), dto);

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
                        ? session.getReservation().getChargingPoint().getPointCode()
                        : (session.getChargingPoint() != null ? session.getChargingPoint().getPointCode() : null),
                session.getStartTime(),
                session.getEndTime(),
                session.getStartSoc() != null ? session.getStartSoc() : 0,
                session.getEndSoc() != null ? session.getEndSoc() : 0,
                session.getEnergyConsumed() != null ? session.getEnergyConsumed() : 0.0,
                session.getCost(),
                session.getStatus(),
                transactionId,
                null, // paymentUrl sẽ set sau nếu có
                session.getStation().getId(),                    // stationId
                session.getDriver().getId(),                     // driverId
                session.getChargingPoint() != null ? session.getChargingPoint().getId() : null  // pointId
        );

        return dto;
    }

    public void checkAndAutoEnd(ChargingSession session, int currentSoc) {
        if (currentSoc == 100) {
            context.getBean(ChargingSessionService.class)
                    .endSession(session.getId());
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
            double power = maxPower * 0.9; // giả lập 60% công suất

            // Tính lượng điện đã nạp thêm
            double addedEnergy = power * durationHours;

            EVDriver driver = session.getDriver();
            Double batteryCapacity = driver.getBatteryCapacity();

            if (batteryCapacity == null || batteryCapacity <= 0) {
                // Fallback: 60 kWh nếu không có dữ liệu
                batteryCapacity = 60.0; // Fallback: 60 kWh
                log.warn("Session {} - Driver {} has no battery capacity, using default 60 kWh",
                        session.getId(), driver.getId());
            }

            // Fallback nếu energyConsumed đang null
            double currentEnergy = session.getEnergyConsumed() != null ? session.getEnergyConsumed() : 0.0;
            double totalEnergy = currentEnergy + addedEnergy;
            int startSoc = session.getStartSoc();
            session.setEnergyConsumed(totalEnergy);

            int newSoc = Math.min(100, (int) (startSoc + (totalEnergy / batteryCapacity) * 100));
            session.setEndSoc(newSoc);

            BigDecimal tempCost = pricingService.calculateLiveChargingFee(session,now);
            session.setCost(tempCost.doubleValue());

            // Cập nhật thời gian
            session.setLastUpdatedTime(now);
            sessionRepo.save(session);

            ChargingSessionDTO dto = toDTO(session);
            messagingTemplate.convertAndSend("/topic/session/" + dto.getId(), dto);

            // Gọi auto end nếu đủ điều kiện
            checkAndAutoEnd(session, newSoc);
        }
    }
}