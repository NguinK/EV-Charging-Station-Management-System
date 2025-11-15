package com.evcharging.controller;

import com.evcharging.dto.ChargingPointResponseDTO;
import com.evcharging.dto.ChargingStationCreateDTO;
import com.evcharging.dto.ChargingStationResponseDTO;
import com.evcharging.dto.DtoMapper;
import com.evcharging.entity.ChargingStation;
import com.evcharging.enums.ConnectorType;
import com.evcharging.enums.StationStatus;
import com.evcharging.repository.ChargingStationRepository;
import com.evcharging.service.ChargingStationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stations")
public class ChargingStationController {

    private final ChargingStationService stationService;
    private final DtoMapper dtoMapper;
    private final ChargingStationRepository chargingStationRepository;

    public ChargingStationController(ChargingStationService stationService,
                                     DtoMapper dtoMapper, ChargingStationRepository chargingStationRepository) {
        this.stationService = stationService;
        this.dtoMapper = dtoMapper;
        this.chargingStationRepository = chargingStationRepository;
    }

    @PostMapping("/createStation")
    public ResponseEntity<ChargingStationResponseDTO> createStation(
            @Valid @RequestBody ChargingStationCreateDTO request) {

        ChargingStationResponseDTO response = stationService.createStation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/allStations")
    public ResponseEntity<List<ChargingStationResponseDTO>> getAllStations() {
        List<ChargingStation> stations = stationService.getAllStations();
        List<ChargingStationResponseDTO> responseList = stations.stream()
                .map(dtoMapper::toChargingStationDTO)
                .toList();

        return ResponseEntity.ok(responseList);
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

    @GetMapping("/{id}/points")
    public List<ChargingPointResponseDTO> getPoints(@PathVariable Long id) {
        ChargingStation station = chargingStationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Station not found"));
        return station.getPoints().stream()
                .map(dtoMapper::toChargingPointDTO )
                .toList();
    }
}