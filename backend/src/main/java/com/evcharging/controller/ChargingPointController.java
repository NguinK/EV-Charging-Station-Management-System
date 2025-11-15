package com.evcharging.controller;

import com.evcharging.dto.ChargingPointCreateDTO;
import com.evcharging.dto.ChargingPointResponseDTO;
import com.evcharging.dto.DtoMapper;
import com.evcharging.entity.ChargingPoint;
import com.evcharging.enums.ConnectorType;
import com.evcharging.enums.ChargingPointStatus;
import com.evcharging.service.ChargingPointService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/charging-points")
@RequiredArgsConstructor
public class ChargingPointController {

    private final ChargingPointService chargingPointService;
    private final DtoMapper dtoMapper;

    //Tạo điểm sạc mới
    @PostMapping("/charging-points")
    public ResponseEntity<ChargingPointResponseDTO> createChargingPoint(
            @RequestBody ChargingPointCreateDTO request) {
        ChargingPoint point = chargingPointService.createChargingPoint(request);
        ChargingPointResponseDTO response = dtoMapper.toChargingPointDTO(point);

        return ResponseEntity.ok(response);
    }

    //Lấy tất cả trụ của trạm
    @GetMapping("/station/{stationId}")
    public ResponseEntity<List<ChargingPointResponseDTO>> getPointsByStation(@PathVariable Long stationId) {
        List<ChargingPointResponseDTO> responseList = chargingPointService.getPointsByStation(stationId);
        return ResponseEntity.ok(responseList);
    }

    //Lấy các điểm sạc có sẵn của một trạm
    @GetMapping("/station/{stationId}/available")
    public ResponseEntity<List<ChargingPointResponseDTO>> getAvailablePoints(@PathVariable Long stationId) {
        List<ChargingPointResponseDTO> points = chargingPointService.getAvailablePoints(stationId);
        return ResponseEntity.ok(points);
    }

    //Lấy thông tin chi tiết một điểm sạc
    @GetMapping("/{pointId}")
    public ResponseEntity<ChargingPointResponseDTO> getChargingPoint(@PathVariable Long pointId) {
        ChargingPointResponseDTO point = chargingPointService.getChargingPointDTO(pointId);
        return ResponseEntity.ok(point);
    }

    //Cập nhật trạng thái điểm sạc
    @PutMapping("/{pointId}/status")
    public ResponseEntity<ChargingPoint> updatePointStatus(
            @PathVariable Long pointId,
            @RequestParam ChargingPointStatus status) {

        ChargingPoint point = chargingPointService.updatePointStatus(pointId, status);
        return ResponseEntity.ok(point);
    }

    //Cập nhật giá của điểm sạc
    @PutMapping("/{pointId}/pricing")
    public ResponseEntity<ChargingPoint> updatePricing(
            @PathVariable Long pointId,
            @RequestParam Double pricePerKwh,
            @RequestParam Double pricePerMinute) {

        ChargingPoint point = chargingPointService.updatePricing(pointId, pricePerKwh, pricePerMinute);
        return ResponseEntity.ok(point);
    }

    //Tìm điểm sạc theo loại connector
    @GetMapping("/search")
    public ResponseEntity<List<ChargingPointResponseDTO>> findByConnectorType(
            @RequestParam ConnectorType connectorType) {

        List<ChargingPointResponseDTO> points = chargingPointService.findByConnectorType(connectorType);
        return ResponseEntity.ok(points);
    }

    //Xóa điểm sạc
    @DeleteMapping("/{pointId}")
    public ResponseEntity<Void> deleteChargingPoint(@PathVariable Long pointId) {
        chargingPointService.deleteChargingPoint(pointId);
        return ResponseEntity.noContent().build();
    }
}