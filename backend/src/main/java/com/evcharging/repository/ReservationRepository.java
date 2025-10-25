package com.evcharging.repository;

import com.evcharging.entity.ChargingPoint;
import com.evcharging.entity.EVDriver;
import com.evcharging.entity.Reservation;
import com.evcharging.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByStationId(Long stationId);
    List<Reservation> findByDriver(EVDriver driver);
    @Query("SELECT r FROM Reservation r WHERE r.chargingPoint = :point AND r.status IN :statuses AND r.startTime < :newExpireTime AND r.expireTime > :newStartTime")
    boolean existsByChargingPointAndStatusInAndTimeOverlap(
            @Param("point") ChargingPoint point,
            @Param("statuses") List<ReservationStatus> statuses,
            @Param("newStartTime") LocalDateTime newStartTime,
            @Param("newExpireTime") LocalDateTime newExpireTime
    );
}