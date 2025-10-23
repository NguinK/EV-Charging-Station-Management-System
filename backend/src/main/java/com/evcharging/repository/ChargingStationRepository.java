package com.evcharging.repository;

import com.evcharging.entity.ChargingStation;
import com.evcharging.enums.ConnectorType;
import com.evcharging.enums.StationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChargingStationRepository extends JpaRepository<ChargingStation, Long> {
    // Ví dụ: tìm theo tên
    List<ChargingStation> findByNameContainingIgnoreCase(String name);

    // Ví dụ: tìm theo trạng thái
    List<ChargingStation> findByStatus(StationStatus status);

    // Tìm theo loại cổng sạc
    //List<ChargingStation> findByConnectorType(ConnectorType connectorType);
    @Query("SELECT DISTINCT s FROM ChargingStation s JOIN s.points p WHERE p.connectorType = :type")
    List<ChargingStation> findByConnectorType(@Param("type") ConnectorType type);
}