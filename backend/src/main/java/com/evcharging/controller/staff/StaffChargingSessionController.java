package com.evcharging.controller.staff;

import com.evcharging.dto.ApiResponse;
import com.evcharging.dto.staff.ChargingSessionResponse;
import com.evcharging.dto.staff.StartSessionRequest;
import com.evcharging.dto.staff.StopSessionRequest;
import com.evcharging.service.staff.StaffChargingSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/staff/sessions")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CS_STAFF')")
@Tag(name = "Staff Charging Session Management", description = "APIs for staff to manage charging sessions")
public class StaffChargingSessionController {
    private final StaffChargingSessionService staffChargingSessionService;

    @PostMapping("/start")
    @Operation(summary = "Start charging session", description = "Start a new charging session for a driver")
    public ResponseEntity<ApiResponse<ChargingSessionResponse>> startSession(
            @Valid @RequestBody StartSessionRequest request) {
        ChargingSessionResponse response = staffChargingSessionService.startSession(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Charging session started successfully", response));
    }

    @PostMapping("/{sessionId}/stop")
    @Operation(summary = "Stop charging session", description = "Stop a running charging session and create payment transaction")
    public ResponseEntity<ApiResponse<ChargingSessionResponse>> stopSession(
            @PathVariable Long sessionId,
            @Valid @RequestBody StopSessionRequest request) {
        ChargingSessionResponse response = staffChargingSessionService.stopSession(sessionId, request);
        return ResponseEntity.ok(ApiResponse.success("Charging session stopped successfully", response));
    }
}
