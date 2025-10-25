package com.evcharging.service;

import com.evcharging.entity.*;
import com.evcharging.enums.*;
import com.evcharging.repository.ChargingPointRepository;
import com.evcharging.repository.ChargingStationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChargingPointService {

    private final ChargingPointRepository chargingPointRepo;
    private final ChargingStationRepository stationRepo;

    /**
     * Tạo điểm sạc mới
     */
    @Transactional
    public ChargingPoint createChargingPoint(Long stationId, String pointCode,
                                             ConnectorType connectorType, Integer maxPower,
                                             Double pricePerKwh, Double pricePerMinute) {

        ChargingStation station = stationRepo.findById(stationId)
                .orElseThrow(() -> new RuntimeException("Station not found"));

        ChargingPoint point = new ChargingPoint();
        point.setStation(station);
        point.setPointCode(pointCode);
        point.setConnectorType(connectorType);
        point.setMaxPower(maxPower);
        point.setSpeed(determineChargingSpeed(maxPower));
        point.setStatus(PointStatus.AVAILABLE);
        point.setPricePerKwh(pricePerKwh);
        point.setPricePerMinute(pricePerMinute);
        point.setCreatedAt(LocalDateTime.now());
        point.setUpdatedAt(LocalDateTime.now());

        return chargingPointRepo.save(point);
    }

    /**
     * Lấy tất cả điểm sạc của một trạm
     */
    public List<ChargingPoint> getPointsByStation(Long stationId) {
        return chargingPointRepo.findByStationId(stationId);
    }

    /**
     * Lấy các điểm sạc có sẵn của một trạm
     */
    public List<ChargingPoint> getAvailablePoints(Long stationId) {
        return chargingPointRepo.findByStationIdAndStatus(stationId, PointStatus.AVAILABLE);
    }

    /**
     * Cập nhật trạng thái điểm sạc
     */
    @Transactional
    public ChargingPoint updatePointStatus(Long pointId, PointStatus status) {
        ChargingPoint point = chargingPointRepo.findById(pointId)
                .orElseThrow(() -> new RuntimeException("Charging point not found"));

        point.setStatus(status);
        point.setUpdatedAt(LocalDateTime.now());

        return chargingPointRepo.save(point);
    }

    /**
     * Cập nhật giá của điểm sạc
     */
    @Transactional
    public ChargingPoint updatePricing(Long pointId, Double pricePerKwh, Double pricePerMinute) {
        ChargingPoint point = chargingPointRepo.findById(pointId)
                .orElseThrow(() -> new RuntimeException("Charging point not found"));

        point.setPricePerKwh(pricePerKwh);
        point.setPricePerMinute(pricePerMinute);
        point.setUpdatedAt(LocalDateTime.now());

        return chargingPointRepo.save(point);
    }


    //Xóa điểm sạc
    @Transactional
    public void deleteChargingPoint(Long pointId) {
        ChargingPoint point = chargingPointRepo.findById(pointId)
                .orElseThrow(() -> new RuntimeException("Charging point not found"));

        if (point.getStatus() == PointStatus.OCCUPIED) {
            throw new RuntimeException("Cannot delete charging point that is currently in use");
        }

        chargingPointRepo.delete(point);
    }

    /**
     * Lấy thông tin chi tiết điểm sạc
     */
    public ChargingPoint getChargingPoint(Long pointId) {
        return chargingPointRepo.findById(pointId)
                .orElseThrow(() -> new RuntimeException("Charging point not found"));
    }

    /**
     * Tìm điểm sạc theo loại connector
     */
    public List<ChargingPoint> findByConnectorType(ConnectorType connectorType) {
        return chargingPointRepo.findByConnectorType(connectorType);
    }

    /**
     * Xác định tốc độ sạc dựa trên công suất
     */
    private ChargingSpeed determineChargingSpeed(Integer maxPower) {
        if (maxPower < 22) {
            return ChargingSpeed.SLOW;
        } else if (maxPower <= 50) {
            return ChargingSpeed.FAST;
        } else {
            return ChargingSpeed.ULTRA_FAST;
        }
    }
}