package com.evcharging.controller.admin;

import com.evcharging.dto.ChargingPointResponseDTO;
import com.evcharging.dto.admin.*;
import com.evcharging.entity.ChargingPoint;
import com.evcharging.entity.ChargingStation;
import com.evcharging.enums.ChargingPointOperation;
import com.evcharging.enums.ChargingStationOperation;
import com.evcharging.enums.TimeGranularity;
import com.evcharging.service.*;
import com.evcharging.service.admin.AdminReportService;
import com.evcharging.service.admin.AdminStationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Admin REST API Controller
 * Chỉ ADMIN mới có quyền truy cập
 */
@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminStationAndPointController {

    private final AdminStationService stationService;
    private final AdminReportService reportService;
    private final ChargingPointService chargingPointService; // ⭐ Inject service

    // ========== DASHBOARD ==========

    //Lấy dashboard tổng quan
    @GetMapping("/dashboard")
    public ResponseEntity<ChargingStationDashboardResponse> getDashboard() {
        return ResponseEntity.ok(stationService.getDashboard());
    }

    // ========== STATION MANAGEMENT ==========

    //Lấy danh sách tất cả trạm
    @GetMapping("/stations")
    public ResponseEntity<List<ChargingStationDetailResponse>> getAllStations() {
        return ResponseEntity.ok(stationService.getAllStationsDetail());
    }

    //Lấy chi tiết một trạm
    @GetMapping("/stations/{id}")
    public ResponseEntity<ChargingStationDetailResponse> getStation(@PathVariable Long id) {
        return ResponseEntity.ok(stationService.getStationDetail(id));
    }

    //Tạo trạm mới
    @PostMapping("/stations")
    public ResponseEntity<ChargingStation> createStation(
            @RequestBody CreateChargingStationRequest request) {
        return ResponseEntity.ok(stationService.createStation(request));
    }

    // ========== CHARGING POINT MANAGEMENT ==========

    //Lấy danh sách điểm sạc của một trạm
    @GetMapping("/stations/{stationId}/points")
    public ResponseEntity<List<ChargingPointResponseDTO>> getStationPoints(
            @PathVariable Long stationId) {
        log.info("Getting charging points for station: {}", stationId);
        List<ChargingPointResponseDTO> points = chargingPointService.getPointsByStation(stationId);
        return ResponseEntity.ok(points);
    }

    //Thêm điểm sạc vào trạm
    @PostMapping("/stations/{stationId}/points")
    public ResponseEntity<ChargingPointResponseDTO> addPoint(
            @PathVariable Long stationId,
            @RequestBody CreateChargingPointRequest request) {
        log.info("Adding point to station: {}", stationId);

        // Tạo point qua AdminStationService
        ChargingPoint createdPoint = stationService.addPointToStation(stationId, request);

        // Convert sang DTO trước khi trả về để tránh lỗi JSON parsing
        ChargingPointResponseDTO dto = chargingPointService.getChargingPointDTO(createdPoint.getId());
        return ResponseEntity.ok(dto);
    }

    //Điều khiển từ xa trạm sạc
    @PostMapping("/stations/{id}/control")
    public ResponseEntity<ChargingStationOperationResponse> controlStation(
            @PathVariable Long id,
            @RequestParam ChargingStationOperation operation) {
        return ResponseEntity.ok(stationService.controlStation(id, operation));
    }

    //Điều khiển từ xa điểm sạc
    @PostMapping("/points/{id}/control")
    public ResponseEntity<ChargingPointOperationResponse> controlPoint(
            @PathVariable Long id,
            @RequestParam ChargingPointOperation operation) {
        return ResponseEntity.ok(stationService.controlPoint(id, operation));
    }

    // ========== REVENUE REPORTS ==========

    //Báo cáo doanh thu theo trạm
    @GetMapping("/reports/revenue/by-station")
    public ResponseEntity<List<RevenueByChargingStationResponse>> getRevenueByStation(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime endDate) {
        return ResponseEntity.ok(reportService.getRevenueByStation(startDate, endDate));
    }

    //Báo cáo doanh thu theo thời gian
    @GetMapping("/reports/revenue/timeline")
    public ResponseEntity<RevenueTimelineResponse> getRevenueTimeline(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime endDate,
            @RequestParam(defaultValue = "DAILY") TimeGranularity granularity) {
        return ResponseEntity.ok(reportService.getRevenueTimeline(startDate, endDate, granularity));
    }

    // ========== USAGE REPORTS ==========

    //Báo cáo sử dụng trạm sạc
    @GetMapping("/reports/usage")
    public ResponseEntity<List<ChargingStationUsageResponse>> getStationUsage(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime endDate) {
        return ResponseEntity.ok(reportService.getStationUsage(startDate, endDate));
    }

    // ========== AI FORECAST ==========

    //AI dự báo nhu cầu sử dụng trạm sạc
    @GetMapping("/forecast/{stationId}")
    public ResponseEntity<ChargingStationDemandForecastResponse> forecastDemand(
            @PathVariable Long stationId) {
        return ResponseEntity.ok(reportService.forecastDemand(stationId));
    }
}