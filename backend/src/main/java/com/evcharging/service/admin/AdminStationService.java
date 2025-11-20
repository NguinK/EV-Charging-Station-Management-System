package com.evcharging.service.admin;

import com.evcharging.dto.DtoMapper;
import com.evcharging.dto.admin.*;
import com.evcharging.entity.*;
import com.evcharging.enums.*;
import com.evcharging.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service quản lý trạm sạc và điểm sạc cho Admin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminStationService {

    private final ChargingStationRepository stationRepo;
    private final ChargingPointRepository pointRepo;
    private final ChargingSessionRepository sessionRepo;
    private final PaymentRepository paymentRepo;
    private final DtoMapper mapper;

    //Lấy dashboard tổng quan tất cả trạm sạc
    public ChargingStationDashboardResponse getDashboard() {
        log.info("Getting station dashboard");

        List<ChargingStation> allStations = stationRepo.findAll();
        List<ChargingPoint> allPoints = pointRepo.findAll();

        // Thống kê tổng quan
        long totalStations = allStations.size();
        long onlineStations = allStations.stream()
                .filter(s -> s.getStatus() == StationStatus.ACTIVE)
                .count();

        long totalPoints = allPoints.size();
        long availablePoints = allPoints.stream()
                .filter(p -> p.getStatus() == ChargingPointStatus.AVAILABLE)
                .count();
        long occupiedPoints = allPoints.stream()
                .filter(p -> p.getStatus() == ChargingPointStatus.OCCUPIED)
                .count();
        long offlinePoints = allPoints.stream()
                .filter(p -> p.getStatus() == ChargingPointStatus.OFFLINE)
                .count();

        // Tổng công suất hệ thống
        int totalPower = allPoints.stream()
                .mapToInt(ChargingPoint::getMaxPower)
                .sum();

        return ChargingStationDashboardResponse.builder()
                .totalStations(totalStations)
                .onlineStations(onlineStations)
                .offlineStations(totalStations - onlineStations)
                .totalPoints(totalPoints)
                .availablePoints(availablePoints)
                .occupiedPoints(occupiedPoints)
                .offlinePoints(offlinePoints)
                .maintenancePoints(totalPoints - availablePoints - occupiedPoints - offlinePoints)
                .totalSystemPower(totalPower)
                .utilizationRate(totalPoints > 0 ? (occupiedPoints * 100.0 / totalPoints) : 0)
                .build();
    }

    //Lấy danh sách tất cả trạm với thông tin chi tiết
    public List<ChargingStationDetailResponse> getAllStationsDetail() {
        log.info("Getting all stations detail");

        List<ChargingStation> stations = stationRepo.findAll();

        return stations.stream()
                .map(this::buildStationDetail)
                .collect(Collectors.toList());
    }

    //Lấy chi tiết một trạm cụ thể
    public ChargingStationDetailResponse getStationDetail(Long stationId) {
        ChargingStation station = stationRepo.findById(stationId)
                .orElseThrow(() -> new RuntimeException("Station not found"));

        return buildStationDetail(station);
    }

    //Điều khiển từ xa: Bật/Tắt trạm
    @Transactional
    public ChargingStationOperationResponse controlStation(Long stationId, ChargingStationOperation operation) {
        log.info("Controlling station {}: {}", stationId, operation);

        ChargingStation station = stationRepo.findById(stationId)
                .orElseThrow(() -> new RuntimeException("Station not found"));

        StationStatus oldStatus = station.getStatus();
        StationStatus newStatus;

        switch (operation) {
            case START:
                newStatus = StationStatus.ACTIVE;
                enableAllPoints(stationId);
                break;

            case STOP:
                newStatus = StationStatus.INACTIVE;
                disableAllPoints(stationId);
                break;



            default:
                throw new RuntimeException("Unknown operation: " + operation);
        }

        station.setStatus(newStatus);
        station.setUpdatedAt(OffsetDateTime.now());
        stationRepo.save(station);

        log.info("Station {} status changed: {} → {}", stationId, oldStatus, newStatus);

        return ChargingStationOperationResponse.builder()
                .success(true)
                .stationId(stationId)
                .stationName(station.getName())
                .oldStatus(oldStatus)
                .newStatus(newStatus)
                .operation(operation)
                .message(String.format("Station %s successfully", operation.name().toLowerCase()))
                .timestamp(OffsetDateTime.now())
                .build();
    }

    //Điều khiển từ xa: Bật/Tắt điểm sạc cụ thể
    @Transactional
    public ChargingPointOperationResponse controlPoint(Long pointId, ChargingPointOperation operation) {
        log.info("Controlling point {}: {}", pointId, operation);

        ChargingPoint point = pointRepo.findById(pointId)
                .orElseThrow(() -> new RuntimeException("ChargingPoint not found"));

        // Kiểm tra nếu đang có session active
        if (operation == ChargingPointOperation.STOP && point.getStatus() == ChargingPointStatus.OCCUPIED) {
            Optional<ChargingSession> activeSession = sessionRepo.findActiveSessionByPoint(pointId);
            if (activeSession.isPresent()) {
                throw new RuntimeException("Cannot stop point with active charging session. " +
                        "Please stop the session first.");
            }
        }

        ChargingPointStatus oldStatus = point.getStatus();
        ChargingPointStatus newStatus;

        switch (operation) {
            case START:
                newStatus = ChargingPointStatus.AVAILABLE;
                break;

            case STOP:
                newStatus = ChargingPointStatus.OFFLINE;
                break;

            case MAINTENANCE:
                newStatus = ChargingPointStatus.MAINTENANCE;
                break;

            default:
                throw new RuntimeException("Unknown operation: " + operation);
        }

        point.setStatus(newStatus);
        point.setUpdatedAt(OffsetDateTime.now());
        pointRepo.save(point);

        log.info("Point {} status changed: {} → {}", pointId, oldStatus, newStatus);

        return ChargingPointOperationResponse.builder()
                .success(true)
                .pointId(pointId)
                .pointCode(point.getPointCode())
                .stationName(point.getStation().getName())
                .oldStatus(oldStatus)
                .newStatus(newStatus)
                .operation(operation)
                .message(String.format("Charging point %s successfully", operation.name().toLowerCase()))
                .timestamp(OffsetDateTime.now())
                .build();
    }

    //Tạo trạm sạc mới
    @Transactional
    public ChargingStation createStation(CreateChargingStationRequest request) {
        log.info("Creating new station: {}", request.getName());

        ChargingStation station = new ChargingStation();
        station.setName(request.getName());
        station.setAddress(request.getAddress());
        station.setLatitude(request.getLatitude());
        station.setLongitude(request.getLongitude());
        // ✅ Set trực tiếp enum
        station.setStatus(StationStatus.ACTIVE);
        station.setOperatorName(request.getOperatorName());
        station.setContactPhone(request.getContactPhone());

        station = stationRepo.save(station);

        log.info("Station created: id={}", station.getId());

        return station;
    }

    //Thêm điểm sạc vào trạm
    @Transactional
    public ChargingPoint addPointToStation(Long stationId, CreateChargingPointRequest request) {
        log.info("Adding point to station {}: {}", stationId, request.getPointCode());

        ChargingStation station = stationRepo.findById(stationId)
                .orElseThrow(() -> new RuntimeException("Station not found"));

        ChargingPoint point = new ChargingPoint();
        point.setStation(station);
        point.setPointCode(request.getPointCode());
        point.setConnectorType(request.getConnectorType());
        point.setMaxPower(request.getMaxPower());
        point.setSpeed(determineSpeed(request.getMaxPower()));
        point.setStatus(ChargingPointStatus.AVAILABLE);
        point.setPricePerKwh(request.getPricePerKwh());
        point.setPricePerMinute(request.getPricePerMinute());

        point = pointRepo.save(point);

        log.info("Point added: id={}", point.getId());

        return point;
    }

    // ========== HELPER METHODS ==========

    private ChargingStationDetailResponse buildStationDetail(ChargingStation station) {
        List<ChargingPoint> points = pointRepo.findByStationId(station.getId());

        long availableCount = points.stream()
                .filter(p -> p.getStatus() == ChargingPointStatus.AVAILABLE)
                .count();

        long occupiedCount = points.stream()
                .filter(p -> p.getStatus() == ChargingPointStatus.OCCUPIED)
                .count();

        int totalPower = points.stream()
                .mapToInt(ChargingPoint::getMaxPower)
                .sum();

        return ChargingStationDetailResponse.builder()
                .id(station.getId())
                .name(station.getName())
                .address(station.getAddress())
                .latitude(station.getLatitude())
                .longitude(station.getLongitude())
                // ✅ Trả về trực tiếp enum, không cần valueOf()
                .status(station.getStatus())
                .operatorName(station.getOperatorName())
                .contactPhone(station.getContactPhone())
                .totalPoints(points.size())
                .availablePoints((int) availableCount)
                .occupiedPoints((int) occupiedCount)
                .totalPower(totalPower)
                .utilizationRate(!points.isEmpty() ? (occupiedCount * 100.0 / points.size()) : 0)
                .points(points)
                .createdAt(station.getCreatedAt())
                .updatedAt(station.getUpdatedAt())
                .build();
    }

    private void enableAllPoints(Long stationId) {
        List<ChargingPoint> points = pointRepo.findByStationId(stationId);
        points.forEach(p -> {
            if (p.getStatus() == ChargingPointStatus.OFFLINE) {
                p.setStatus(ChargingPointStatus.AVAILABLE);
            }
        });
        pointRepo.saveAll(points);
    }

    private void disableAllPoints(Long stationId) {
        List<ChargingPoint> points = pointRepo.findByStationId(stationId);
        points.forEach(p -> {
            if (p.getStatus() != ChargingPointStatus.OCCUPIED) {
                p.setStatus(ChargingPointStatus.OFFLINE);
            }
        });
        pointRepo.saveAll(points);
    }

    private void setAllPointsMaintenance(Long stationId) {
        List<ChargingPoint> points = pointRepo.findByStationId(stationId);
        points.forEach(p -> {
            if (p.getStatus() != ChargingPointStatus.OCCUPIED) {
                p.setStatus(ChargingPointStatus.MAINTENANCE);
            }
        });
        pointRepo.saveAll(points);
    }

    private ChargingSpeed determineSpeed(Integer maxPower) {
        if (maxPower < 22) return ChargingSpeed.SLOW;
        if (maxPower <= 50) return ChargingSpeed.FAST;
        return ChargingSpeed.ULTRA_FAST;
    }
}