package com.evcharging.repository;

import com.evcharging.entity.ChargingSession;
import com.evcharging.enums.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChargingSessionRepository extends JpaRepository<ChargingSession, Long> {
    List<ChargingSession> findByStatus(SessionStatus sessionStatus);

    List<ChargingSession> findByStationIdIn(List<Long> stationIds);
}