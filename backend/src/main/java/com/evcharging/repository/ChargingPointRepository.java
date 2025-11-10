package com.evcharging.repository;

import com.evcharging.entity.ChargingPoint;
import com.evcharging.enums.ChargingPointStatus;
import com.evcharging.enums.ConnectorType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChargingPointRepository extends JpaRepository<ChargingPoint, Long> {


    //Tìm tất cả điểm sạc của một trạm
    List<ChargingPoint> findByStationId(Long stationId);

    //Tìm điểm sạc theo loại connector
    List<ChargingPoint> findByConnectorType(ConnectorType connectorType);

    //Tìm điểm sạc theo trạng thái
    List<ChargingPoint> findByStatus(ChargingPointStatus status);

    //Tìm điểm sạc theo trạm và trạng thái
    @Query("SELECT cp FROM ChargingPoint cp WHERE cp.station.id = :stationId AND cp.status = :status")
    List<ChargingPoint> findByStationIdAndStatus(@Param("stationId") Long stationId,
                                                 @Param("status") ChargingPointStatus status);


    //Đếm số điểm sạc available của một trạm
    @Query("SELECT COUNT(cp) FROM ChargingPoint cp WHERE cp.station.id = :stationId AND cp.status = 'AVAILABLE'")
    Long countAvailablePointsByStation(@Param("stationId") Long stationId);


    //Tìm điểm sạc theo mã code
    Optional<ChargingPoint> findByPointCode(String pointCode);

    Optional<ChargingPoint> findByStationIdAndId(Long stationId, Long id);

    //Tìm các điểm sạc available theo loại connector
    @Query("SELECT cp FROM ChargingPoint cp WHERE cp.connectorType = :type AND cp.status = 'AVAILABLE'")
    List<ChargingPoint> findAvailableByConnectorType(@Param("type") ConnectorType type);


    // Tìm điểm sạc theo khoảng giá
    @Query("SELECT cp FROM ChargingPoint cp WHERE cp.pricePerKwh BETWEEN :minPrice AND :maxPrice")
    List<ChargingPoint> findByPriceRange(@Param("minPrice") Double minPrice,
                                         @Param("maxPrice") Double maxPrice);
}