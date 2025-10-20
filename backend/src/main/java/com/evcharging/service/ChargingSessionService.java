package com.evcharging.service;

import com.evcharging.entity.ChargingSession;
import com.evcharging.entity.Reservation;
import com.evcharging.enums.ReservationStatus;
import com.evcharging.enums.SessionStatus;
import com.evcharging.repository.ChargingSessionRepository;
import com.evcharging.repository.ReservationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ChargingSessionService {

    private final ChargingSessionRepository sessionRepo;
    private final ReservationRepository reservationRepo;

    public ChargingSessionService(ChargingSessionRepository sessionRepo,
                                  ReservationRepository reservationRepo) {
        this.sessionRepo = sessionRepo;
        this.reservationRepo = reservationRepo;
    }

    // Bắt đầu phiên sạc
    public ChargingSession startSession(Long reservationId, int startSoc) {
        Reservation reservation = reservationRepo.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        if (reservation.getStatus() != ReservationStatus.CONFIRMED) {
            throw new RuntimeException("Reservation not valid for charging");
        }

        ChargingSession session = new ChargingSession();
        session.setReservation(reservation);
        session.setDriver(reservation.getDriver());
        session.setStation(reservation.getStation());
        session.setStartTime(LocalDateTime.now());
        session.setStartSoc(startSoc);
        session.setStatus(SessionStatus.CHARGING);

        return sessionRepo.save(session);
    }

    // Kết thúc phiên sạc
    public ChargingSession endSession(Long sessionId, int endSoc, double energy, double cost) {
        ChargingSession session = sessionRepo.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        session.setEndTime(LocalDateTime.now());
        session.setEndSoc(endSoc);
        session.setEnergyConsumed(energy);
        session.setCost(cost);
        session.setStatus(SessionStatus.COMPLETED);

        return sessionRepo.save(session);
    }
}