package com.evcharging.service;

import com.evcharging.entity.ChargingSession;
import com.evcharging.entity.Reservation;
import com.evcharging.entity.Transaction;
import com.evcharging.enums.*;
import com.evcharging.repository.ChargingSessionRepository;
import com.evcharging.repository.ReservationRepository;
import com.evcharging.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ChargingSessionService {

    private final ChargingSessionRepository sessionRepo;
    private final ReservationRepository reservationRepo;
    private final TransactionRepository transactionRepo;

    public ChargingSessionService(ChargingSessionRepository sessionRepo,
                                  ReservationRepository reservationRepo,
                                  TransactionRepository transactionRepo) {
        this.sessionRepo = sessionRepo;
        this.reservationRepo = reservationRepo;
        this.transactionRepo = transactionRepo;
    }

    /**
     * Bắt đầu phiên sạc từ một Reservation hợp lệ
     */
    @Transactional
    public ChargingSession startSession(Long reservationId, int startSoc) {
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

        return sessionRepo.save(session);
    }

    /**
     * Kết thúc phiên sạc, cập nhật thông tin và tạo Transaction
     */
    @Transactional
    public ChargingSession endSession(Long sessionId, int endSoc, double energy, double cost) {
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

        // Tạo Transaction gắn với session
        Transaction tx = new Transaction();
        tx.setSession(session);
        tx.setDriver(session.getDriver());
        tx.setTimestamp(LocalDateTime.now());
        tx.setAmount(cost);
        tx.setCurrency("VND");
        tx.setType(TransactionType.PAYMENT);
        tx.setPaymentMethod(PaymentMethod.E_WALLET); // hoặc BANKING/CASH tuỳ
        tx.setStatus(TransactionStatus.PENDING);
        tx.setInvoiceNumber("INV-" + System.currentTimeMillis());

        transactionRepo.save(tx);

        return sessionRepo.save(session);
    }
}