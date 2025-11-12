package com.evcharging.controller;

import com.evcharging.dto.admin.ChargingPointResponse;
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

    /**
     * POST /api/charging-points
     * Tạo điểm sạc mới
     */
    @PostMapping
    public ResponseEntity<ChargingPoint> createChargingPoint(
            @RequestParam Long stationId,
            @RequestParam String pointCode,
            @RequestParam ConnectorType connectorType,
            @RequestParam Integer maxPower,
            @RequestParam Double pricePerKwh,
            @RequestParam Double pricePerMinute) {

        ChargingPoint point = chargingPointService.createChargingPoint(
                stationId, pointCode, connectorType, maxPower, pricePerKwh, pricePerMinute);
        return ResponseEntity.ok(point);
    }

    /**
     * GET /api/charging-points/station/{stationId}
     * Lấy tất cả điểm sạc của một trạm
     */
    @GetMapping("/station/{stationId}")
    public ResponseEntity<List<ChargingPointResponse>> getPointsByStation(@PathVariable Long stationId) {
        List<ChargingPointResponse> points = chargingPointService.getPointsByStation(stationId);
        return ResponseEntity.ok(points);
    }

    /**
     * GET /api/charging-points/station/{stationId}/available
     * Lấy các điểm sạc có sẵn của một trạm
     */
    @GetMapping("/station/{stationId}/available")
    public ResponseEntity<List<ChargingPointResponse>> getAvailablePoints(@PathVariable Long stationId) {
        List<ChargingPointResponse> points = chargingPointService.getAvailablePoints(stationId);
        return ResponseEntity.ok(points);
    }

    /**
     * GET /api/charging-points/{pointId}
     * Lấy thông tin chi tiết một điểm sạc
     */
    @GetMapping("/{pointId}")
    public ResponseEntity<ChargingPointResponse> getChargingPoint(@PathVariable Long pointId) {
        ChargingPointResponse point = chargingPointService.getChargingPointDTO(pointId);
        return ResponseEntity.ok(point);
    }

    /**
     * PUT /api/charging-points/{pointId}/status
     * Cập nhật trạng thái điểm sạc
     */
    @PutMapping("/{pointId}/status")
    public ResponseEntity<ChargingPoint> updatePointStatus(
            @PathVariable Long pointId,
            @RequestParam ChargingPointStatus status) {

        ChargingPoint point = chargingPointService.updatePointStatus(pointId, status);
        return ResponseEntity.ok(point);
    }

    /**
     * PUT /api/charging-points/{pointId}/pricing
     * Cập nhật giá của điểm sạc
     */
    @PutMapping("/{pointId}/pricing")
    public ResponseEntity<ChargingPoint> updatePricing(
            @PathVariable Long pointId,
            @RequestParam Double pricePerKwh,
            @RequestParam Double pricePerMinute) {

        ChargingPoint point = chargingPointService.updatePricing(pointId, pricePerKwh, pricePerMinute);
        return ResponseEntity.ok(point);
    }

    /**
     * GET /api/charging-points/search
     * Tìm điểm sạc theo loại connector
     */
    @GetMapping("/search")
    public ResponseEntity<List<ChargingPointResponse>> findByConnectorType(
            @RequestParam ConnectorType connectorType) {

        List<ChargingPointResponse> points = chargingPointService.findByConnectorType(connectorType);
        return ResponseEntity.ok(points);
    }

    /**
     * DELETE /api/charging-points/{pointId}
     * Xóa điểm sạc
     */
    @DeleteMapping("/{pointId}")
    public ResponseEntity<Void> deleteChargingPoint(@PathVariable Long pointId) {
        chargingPointService.deleteChargingPoint(pointId);
        return ResponseEntity.noContent().build();
    }
}