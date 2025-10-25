package com.evcharging.controller;

import com.evcharging.entity.ChargingStation;
import com.evcharging.enums.ConnectorType;
import com.evcharging.enums.StationStatus;
import com.evcharging.service.ChargingStationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stations")
public class ChargingStationController {

    private final ChargingStationService stationService;

    public ChargingStationController(ChargingStationService stationService) {
        this.stationService = stationService;
    }

    @GetMapping
    public ResponseEntity<List<ChargingStation>> getAllStations() {
        return ResponseEntity.ok(stationService.getAllStations());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ChargingStation>> getStationsByStatus(@PathVariable StationStatus status) {
        return ResponseEntity.ok(stationService.getStationsByStatus(status));
    }

    @GetMapping("/connector/{type}")
    public ResponseEntity<List<ChargingStation>> getStationsByConnector(@PathVariable ConnectorType type) {
        return ResponseEntity.ok(stationService.getStationsByConnectorType(type));
    }

    @GetMapping("/nearby")
    public ResponseEntity<List<ChargingStation>> getStationsNearby(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam double radiusKm) {
        return ResponseEntity.ok(stationService.getStationsNearby(lat, lng, radiusKm));
    }
}