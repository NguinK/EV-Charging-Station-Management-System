package com.evcharging.controller;

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

    @GetMapping("/station/{stationId}")
    public ResponseEntity<List<ChargingPoint>> getPointsByStation(@PathVariable Long stationId) {
        List<ChargingPoint> points = chargingPointService.getPointsByStation(stationId);
        return ResponseEntity.ok(points);
    }


    @GetMapping("/station/{stationId}/available")
    public ResponseEntity<List<ChargingPoint>> getAvailablePoints(@PathVariable Long stationId) {
        List<ChargingPoint> points = chargingPointService.getAvailablePoints(stationId);
        return ResponseEntity.ok(points);
    }


     //Lấy thông tin điểm sạc

    @GetMapping("/{pointId}")
    public ResponseEntity<ChargingPoint> getChargingPoint(@PathVariable Long pointId) {
        ChargingPoint point = chargingPointService.getChargingPoint(pointId);
        return ResponseEntity.ok(point);
    }


     // Cập nhật trạng thái điểm sạc
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