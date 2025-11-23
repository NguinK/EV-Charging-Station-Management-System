package com.evcharging.repository;

import com.evcharging.entity.ChargingSession;
import com.evcharging.enums.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface ChargingSessionRepository extends JpaRepository<ChargingSession, Long> {
    @Query("SELECT cs FROM ChargingSession cs WHERE cs.driver.id = :driverId")
    List<ChargingSession> findByAccountId(@Param("driverId") Long driverId);
    List<ChargingSession> findByStationId(Long stationId);
    List<ChargingSession> findByStatus(SessionStatus status);


    @Query("SELECT cs FROM ChargingSession cs " +
            "WHERE cs.station.id = :stationId " +
            "AND cs.startTime BETWEEN :startDate AND :endDate")
    List<ChargingSession> findByStationAndDateRange(
            @Param("stationId") Long stationId,
            @Param("startDate") OffsetDateTime startDate,
            @Param("endDate") OffsetDateTime endDate);

    @Query("SELECT cs FROM ChargingSession cs " +
            "WHERE (:driverId IS NULL OR cs.driver.id = :driverId) " +
            "AND cs.startTime BETWEEN :startDate AND :endDate " +
            "ORDER BY cs.startTime ASC")
    List<ChargingSession> findByAccountIdAndDateRange(
            @Param("driverId") Long driverId,
            @Param("startDate") OffsetDateTime startDate,
            @Param("endDate") OffsetDateTime endDate);

    @Query("SELECT cs FROM ChargingSession cs " +
            "WHERE cs.chargingPoint.id = :pointId AND cs.status = 'ACTIVE'")
    Optional<ChargingSession> findActiveSessionByPoint(@Param("pointId") Long pointId);

}