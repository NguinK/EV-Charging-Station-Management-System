package com.evcharging.controller.staff;

import com.evcharging.dto.ApiResponse;
import com.evcharging.dto.staff.StationSummaryResponse;
import com.evcharging.service.staff.StaffStationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/staff/stations")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CS_STAFF')")
@Tag(name = "Staff Station Management", description = "APIs for staff to manage their assigned stations")
public class StaffStationController {
    private final StaffStationService staffStationService;
    @GetMapping("/mine")
    @Operation(summary = "Get my assigned stations", description = "Returns all stations assigned to the logged-in staff member")
    public ResponseEntity<ApiResponse<List<StationSummaryResponse>>> getMyStations() {
         List<StationSummaryResponse> stations = staffStationService.getMyStations();
         return ResponseEntity.ok(ApiResponse.success("Stations retrived successfully", stations));
     }
}
