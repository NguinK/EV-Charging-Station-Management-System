package com.evcharging.controller;

import com.evcharging.entity.ChargingPoint;
import com.evcharging.enums.ConnectorType;
import com.evcharging.enums.PointStatus;
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
     * Tạo điểm sạc mới
     * POST /api/charging-points
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
     * Lấy tất cả điểm sạc của một trạm
     * GET /api/charging-points/station/{stationId}
     */
    @GetMapping("/station/{stationId}")
    public ResponseEntity<List<ChargingPoint>> getPointsByStation(@PathVariable Long stationId) {
        List<ChargingPoint> points = chargingPointService.getPointsByStation(stationId);
        return ResponseEntity.ok(points);
    }

    /**
     * Lấy các điểm sạc available của một trạm
     * GET /api/charging-points/station/{stationId}/available
     */
    @GetMapping("/station/{stationId}/available")
    public ResponseEntity<List<ChargingPoint>> getAvailablePoints(@PathVariable Long stationId) {
        List<ChargingPoint> points = chargingPointService.getAvailablePoints(stationId);
        return ResponseEntity.ok(points);
    }

    /**
     * Lấy thông tin điểm sạc
     * GET /api/charging-points/{pointId}
     */
    @GetMapping("/{pointId}")
    public ResponseEntity<ChargingPoint> getChargingPoint(@PathVariable Long pointId) {
        ChargingPoint point = chargingPointService.getChargingPoint(pointId);
        return ResponseEntity.ok(point);
    }

    /**
     * Cập nhật trạng thái điểm sạc
     * PUT /api/charging-points/{pointId}/status
     */
    @PutMapping("/{pointId}/status")
    public ResponseEntity<ChargingPoint> updatePointStatus(
            @PathVariable Long pointId,
            @RequestParam PointStatus status) {

        ChargingPoint point = chargingPointService.updatePointStatus(pointId, status);
        return ResponseEntity.ok(point);
    }

    /**
     * Cập nhật giá của điểm sạc
     * PUT /api/charging-points/{pointId}/pricing
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
     * Tìm điểm sạc theo loại connector
     * GET /api/charging-points/search
     */
    @GetMapping("/search")
    public ResponseEntity<List<ChargingPoint>> findByConnectorType(
            @RequestParam ConnectorType connectorType) {

        List<ChargingPoint> points = chargingPointService.findByConnectorType(connectorType);
        return ResponseEntity.ok(points);
    }

    /**
     * Xóa điểm sạc
     * DELETE /api/charging-points/{pointId}
     */
    @DeleteMapping("/{pointId}")
    public ResponseEntity<Void> deleteChargingPoint(@PathVariable Long pointId) {
        chargingPointService.deleteChargingPoint(pointId);
        return ResponseEntity.noContent().build();
    }
}