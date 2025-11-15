package com.evcharging.service;

import com.evcharging.dto.ChargingPointCreateDTO;
import com.evcharging.dto.ChargingPointResponseDTO;
import com.evcharging.dto.DtoMapper;
import com.evcharging.entity.*;
import com.evcharging.enums.*;
import com.evcharging.repository.ChargingPointRepository;
import com.evcharging.repository.ChargingStationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChargingPointService {

    private final ChargingPointRepository chargingPointRepo;
    private final ChargingStationRepository stationRepo;
    private final DtoMapper mapper;

    //Tạo điểm sạc mới
    @Transactional
    public ChargingPoint createChargingPoint(ChargingPointCreateDTO req) {
        ChargingStation station = stationRepo.findById(req.getStationId())
                .orElseThrow(() -> new RuntimeException("Station not found"));

        ChargingPoint point = new ChargingPoint();
        point.setStation(station);
        point.setPointCode(req.getPointCode());
        point.setConnectorType(req.getConnectorType());
        point.setMaxPower(req.getMaxPower());
        point.setSpeed(determineChargingSpeed(req.getMaxPower()));
        point.setStatus(ChargingPointStatus.AVAILABLE);
        point.setPricePerKwh(req.getPricePerKwh());
        point.setPricePerMinute(req.getPricePerMinute());
        point.setCreatedAt(OffsetDateTime.now());
        point.setUpdatedAt(OffsetDateTime.now());

        return chargingPointRepo.save(point);
    }

    //Lấy tất cả điểm sạc của một trạm (trả về DTO)
    public List<ChargingPointResponseDTO> getPointsByStation(Long stationId) {
        List<ChargingPoint> points = chargingPointRepo.findByStationId(stationId);
        return mapper.toChargingPointDTOList(points);
    }

    //Lấy các điểm sạc có sẵn của một trạm (trả về DTO)
    public List<ChargingPointResponseDTO> getAvailablePoints(Long stationId) {
        List<ChargingPoint> points = chargingPointRepo.findByStationIdAndStatus(
                stationId, ChargingPointStatus.AVAILABLE);
        return mapper.toChargingPointDTOList(points);
    }

    //Lấy thông tin chi tiết điểm sạc (trả về DTO)
    public ChargingPointResponseDTO getChargingPointDTO(Long pointId) {
        ChargingPoint point = chargingPointRepo.findById(pointId)
                .orElseThrow(() -> new RuntimeException("Charging point not found"));
        return mapper.toChargingPointDTO(point);
    }

    //Lấy thông tin chi tiết điểm sạc (trả về Entity - dùng cho internal)
    public ChargingPoint getChargingPoint(Long pointId) {
        return chargingPointRepo.findById(pointId)
                .orElseThrow(() -> new RuntimeException("Charging point not found"));
    }

    //Cập nhật trạng thái điểm sạc
    @Transactional
    public ChargingPoint updatePointStatus(Long pointId, ChargingPointStatus status) {
        ChargingPoint point = chargingPointRepo.findById(pointId)
                .orElseThrow(() -> new RuntimeException("Charging point not found"));
        point.setStatus(status);
        point.setUpdatedAt(OffsetDateTime.now());
        return chargingPointRepo.save(point);
    }

    //Cập nhật giá của điểm sạc
    @Transactional
    public ChargingPoint updatePricing(Long pointId, Double pricePerKwh, Double pricePerMinute) {
        ChargingPoint point = chargingPointRepo.findById(pointId)
                .orElseThrow(() -> new RuntimeException("Charging point not found"));

        point.setPricePerKwh(pricePerKwh);
        point.setPricePerMinute(pricePerMinute);
        point.setUpdatedAt(OffsetDateTime.now());

        return chargingPointRepo.save(point);
    }

    //Xóa điểm sạc
    @Transactional
    public void deleteChargingPoint(Long pointId) {
        ChargingPoint point = chargingPointRepo.findById(pointId)
                .orElseThrow(() -> new RuntimeException("Charging point not found"));

        if (point.getStatus() == ChargingPointStatus.OCCUPIED) {
            throw new RuntimeException("Cannot delete charging point that is currently in use");
        }

        chargingPointRepo.delete(point);
    }

    //Tìm điểm sạc theo loại connector (trả về DTO)
    public List<ChargingPointResponseDTO> findByConnectorType(ConnectorType connectorType) {
        List<ChargingPoint> points = chargingPointRepo.findByConnectorType(connectorType);
        return mapper.toChargingPointDTOList(points);
    }

    //Xác định tốc độ sạc dựa trên công suất
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