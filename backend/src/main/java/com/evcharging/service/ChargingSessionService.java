package com.evcharging.service;

import com.evcharging.dto.ChargingSessionDTO;
import com.evcharging.entity.ChargingPoint;
import com.evcharging.entity.ChargingSession;
import com.evcharging.entity.Reservation;
import com.evcharging.entity.Transaction;
import com.evcharging.enums.*;
import com.evcharging.repository.ChargingSessionRepository;
import com.evcharging.repository.ReservationRepository;
import com.evcharging.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChargingSessionService {

    private final ChargingSessionRepository sessionRepo;
    private final ReservationRepository reservationRepo;
    private final TransactionRepository transactionRepo;
    private final NotificationService notificationService;

    public ChargingSessionService(ChargingSessionRepository sessionRepo,
                                  ReservationRepository reservationRepo,
                                  TransactionRepository transactionRepo,
                                  NotificationService notificationService) {
        this.sessionRepo = sessionRepo;
        this.reservationRepo = reservationRepo;
        this.transactionRepo = transactionRepo;
        this.notificationService = notificationService;
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
        reservation.setStatus(ReservationStatus.USED);

        ChargingSession session = new ChargingSession();
        session.setReservation(reservation);
        session.setDriver(reservation.getDriver());
        session.setStation(reservation.getStation());
        session.setStartTime(LocalDateTime.now());
        session.setStartSoc(startSoc);
        session.setStatus(SessionStatus.CHARGING);

        session = sessionRepo.save(session);

        return toDTO(session); // trả về DTO
    }

    // Kết thúc phiên sạc thủ công, cập nhật thông tin và tạo Transaction
    @Transactional
    public ChargingSessionDTO endSession(Long sessionId, int endSoc, double energy, double cost) {
        ChargingSession session = sessionRepo.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));

        if (session.getStatus() != SessionStatus.CHARGING) {
            throw new IllegalStateException("Session is not active");
        }

        session.setEndTime(LocalDateTime.now());
        session.setEndSoc(endSoc);
        session.setEnergyConsumed(energy);
        session.setCost(cost);
        session.setStatus(SessionStatus.COMPLETED);

        notificationService.sendChargingComplete(session.getDriver());

        // Tạo Transaction gắn với session
        Transaction tx = new Transaction();
        tx.setSession(session);
        tx.setDriver(session.getDriver());
        tx.setTimestamp(LocalDateTime.now());
        tx.setAmount(cost);
        tx.setCurrency("VND");
        tx.setType(TransactionType.PAYMENT);
        tx.setPaymentMethod(PaymentMethod.EWALLET); // hoặc BANKING/CASH tuỳ
        tx.setStatus(TransactionStatus.PENDING);
        tx.setInvoiceNumber("INV-" + System.currentTimeMillis());

        transactionRepo.save(tx);

        session = sessionRepo.save(session);

        return toDTO(session); // trả về DTO
    }

    // Mapper entity -> DTO
    private ChargingSessionDTO toDTO(ChargingSession session) {
        return new ChargingSessionDTO(
                session.getId(),
                session.getStation().getName(),
                session.getDriver().getFullName(),
                session.getReservation() != null && session.getReservation().getChargingPoint() != null
                        ? session.getReservation().getChargingPoint().getPointCode()
                        : null,
                session.getStartTime(),
                session.getEndTime(),
                session.getStartSoc(),
                session.getEndSoc(),
                session.getEnergyConsumed(),
                session.getCost(),
                session.getStatus()
        );
    }
    public void checkAndAutoEnd(ChargingSession session, int currentSoc, double currentPower) {
        if (currentSoc >= 100 && currentPower < 1.0) {
            endSession(session.getId(), currentSoc, session.getEnergyConsumed(), session.getCost());
        }
    }
    @Scheduled(fixedRate = 5000) // chạy mỗi 5 giây
    public void autoUpdateChargingSessions() {
        List<ChargingSession> activeSessions = sessionRepo.findByStatus(SessionStatus.CHARGING);

        for (ChargingSession session : activeSessions) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime lastUpdate = session.getLastUpdatedTime() != null
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
            double currentEnergy = session.getEnergyConsumed() ;
            double totalEnergy = currentEnergy + addedEnergy;
            session.setEnergyConsumed(totalEnergy);

            // Giả lập dung lượng pin xe (ví dụ 50 kWh)
            double batteryCapacity = 50.0;
            int startSoc =  session.getStartSoc();
            int newSoc = Math.min(100, (int)(startSoc + (totalEnergy / batteryCapacity) * 100));
            session.setEndSoc(newSoc);

            // Cập nhật thời gian
            session.setLastUpdatedTime(now);
            sessionRepo.save(session);

            // Gọi auto end nếu đủ điều kiện
            checkAndAutoEnd(session, newSoc, power);
        }
    }
}