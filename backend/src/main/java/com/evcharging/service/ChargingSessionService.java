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
//       session.setDriver(reservation.getDriver());
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
            return toDTO(session);
        }

        // set end time
        session.setEndTime(OffsetDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));

        // default endSoc = 100 nếu FE không gửi
        int endSoc = session.getEndSoc() != null ? session.getEndSoc() : 100;
        session.setEndSoc(endSoc);

        //  1. Tính năng lượng đã sạc
        double energy = session.getDriver().getBatteryCapacity()
                * (endSoc - session.getStartSoc()) / 100.0;
        session.setEnergyConsumed(Math.max(0, energy));

        // 2. Tính tiền
        BigDecimal finalCost = pricingService.calculateChargingFee(session);
        session.setCost(finalCost.doubleValue());

        //  3. Mở trụ
        ChargingPoint point = session.getChargingPoint();
        point.setStatus(ChargingPointStatus.AVAILABLE);
        chargingPointRepo.save(point);

        //  4. Tạo transaction 1 lần
        if (session.getTransaction() == null) {
            Transaction tx = transactionService.createTransaction(session, finalCost, TransactionStatus.PENDING);
            session.setTransaction(tx);
        }

        session.setStatus(SessionStatus.COMPLETED);
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
        if (currentSoc >= 100) {
            context.getBean(ChargingSessionService.class)
                    .endSession(session.getId());
        }
    }

    @Scheduled(fixedRate = 3000)
    @Transactional
    public void autoUpdateChargingSessions() {
        List<ChargingSession> activeSessions = sessionRepo.findByStatus(SessionStatus.CHARGING);

        for (ChargingSession session : activeSessions) {
            OffsetDateTime now = OffsetDateTime.now();
            OffsetDateTime lastUpdate = session.getLastUpdatedTime() != null
                    ? session.getLastUpdatedTime()
                    : session.getStartTime();

            double durationHours = Duration.between(lastUpdate, now).toMillis() / (1000.0 * 60 * 60);

            ChargingPoint point = session.getChargingPoint();
            int maxPower = (point != null && point.getMaxPower() != null) ? point.getMaxPower() : 30;
            double power = maxPower * 0.9;

            EVDriver driver = session.getDriver();
            double batteryCapacity = (driver.getBatteryCapacity() != null && driver.getBatteryCapacity() > 0)
                    ? driver.getBatteryCapacity() : 60.0;

            double currentEnergy = session.getEnergyConsumed() != null ? session.getEnergyConsumed() : 0.0;
            double totalEnergy = currentEnergy + power * durationHours;
            int startSoc = session.getStartSoc() != null ? session.getStartSoc() : 0;
            int newSoc = Math.min(100, (int) (startSoc + (totalEnergy / batteryCapacity) * 100));

            session.setEnergyConsumed(totalEnergy);
            session.setEndSoc(newSoc);
            session.setLastUpdatedTime(now);

            // Cập nhật phí tạm nếu chưa đầy pin
            if (newSoc < 100) {
                BigDecimal tempCost = pricingService.calculateLiveChargingFee(session, now);
                session.setCost(tempCost.doubleValue());
            }

            sessionRepo.save(session);

            ChargingSessionDTO dto = toDTO(session);
            messagingTemplate.convertAndSend("/topic/session/" + dto.getId(), dto);

            // Gọi autoCheckEnd để kết thúc nếu đầy pin
            checkAndAutoEnd(session,newSoc);
        }
    }


}