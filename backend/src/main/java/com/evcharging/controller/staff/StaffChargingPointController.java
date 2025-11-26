package com.evcharging.controller.staff;

import com.evcharging.dto.ApiResponse;
import com.evcharging.service.staff.StaffChargingPointService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/staff/chargers")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CS_STAFF')")
@Tag(name = "Staff Charging Point Management", description = "APIs for staff to manage charging points status")
public class StaffChargingPointController {
    private final StaffChargingPointService staffChargingPointService;

    @PostMapping("/{chargingPointId}/set-available")
    @Operation(summary = "Set charging point available", description = "Set a charging point status to AVAILABLE")
    public ResponseEntity<ApiResponse<Void>> setChargerAvailable(@PathVariable Long chargerId) {
        staffChargingPointService.setChargingPointAvailable(chargerId);
        return ResponseEntity.ok(ApiResponse.success("Charger set to AVAILABLE", null));
    }

    @PostMapping("/{chargerId}/set-out-of-service")
    @Operation(summary = "Set charger out of service", description = "Set a charger status to OUT_OF_SERVICE for maintenance")
    public ResponseEntity<ApiResponse<Void>> setChargerOutOfService(
            @PathVariable Long chargerId,
            @RequestParam(required = false) String reason) {
        staffChargingPointService.setChargerOutOfService(chargerId, reason);
        return ResponseEntity.ok(ApiResponse.success("Charger set to OUT_OF_SERVICE", null));
    }
}
