package com.evcharging.repository;

import com.evcharging.entity.ChargingSession;
import com.evcharging.enums.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface ChargingSessionRepository extends JpaRepository<ChargingSession, Long> {

    // Tìm session đang active (CHARGING) của 1 driver
    Optional<ChargingSession> findByDriverIdAndStatus(Long driverId, SessionStatus status);

    // Lấy tất cả session của 1 driver
    List<ChargingSession> findAllByDriverId(Long driverId);

    // Lấy tất cả session của 1 station
    List<ChargingSession> findAllByStationId(Long stationId);

    // Tìm session theo reservation
    Optional<ChargingSession> findByReservationId(Long reservationId);
}