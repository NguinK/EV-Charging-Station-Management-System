package com.evcharging.repository;

import com.evcharging.entity.ChargingPoint;
import com.evcharging.entity.EVDriver;
import com.evcharging.entity.Reservation;
import com.evcharging.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {


    List<Reservation> findByStatusAndExpireTimeBefore(ReservationStatus status, OffsetDateTime time);

    List<Reservation> findByDriver(EVDriver driver);

    @Query("SELECT COUNT(r)>0 FROM Reservation r WHERE r.chargingPoint = :point AND r.status IN :statuses AND r.startTime < :newExpireTime AND r.expireTime > :newStartTime")
    Boolean existsByChargingPointAndStatusInAndTimeOverlap(
            @Param("point") ChargingPoint point,
            @Param("statuses") List<ReservationStatus> statuses,
            @Param("newStartTime") OffsetDateTime newStartTime,
            @Param("newExpireTime") OffsetDateTime newExpireTime
    );

    @Query("SELECT r FROM Reservation r WHERE r.station.id = :stationId " +
            "AND r.status IN :statuses " +
            "AND DATE(r.startTime) = DATE(:date)")
    List<Reservation> findByStationIdAndStatusInAndDate(
            @Param("stationId") Long stationId,
            @Param("statuses") List<ReservationStatus> statuses,
            @Param("date") OffsetDateTime date
    );

    List<Reservation> findByStationIdInAndStatusIn(List<Long> stationIds, List<ReservationStatus> statuses);
}