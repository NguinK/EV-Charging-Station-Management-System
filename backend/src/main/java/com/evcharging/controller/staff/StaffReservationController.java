package com.evcharging.controller.staff;

import com.evcharging.dto.ApiResponse;
import com.evcharging.dto.staff.CheckInReservationRequest;
import com.evcharging.dto.staff.ReservationSummaryResponse;
import com.evcharging.service.staff.StaffReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CS_STAFF')")
@Tag(name = "Staff Reservation Management", description = "APIs for staff to manage reservations")
public class StaffReservationController {
    private final StaffReservationService staffReservationService;

    @GetMapping("/stations/{stationId}/reservations/today")
    @Operation(summary = "Get today's reservations", description = "Returns all reservations for today at the specified station")
    public ResponseEntity<ApiResponse<List<ReservationSummaryResponse>>> getTodayReservations(
            @PathVariable Long stationId) {
        List<ReservationSummaryResponse> reservations = staffReservationService.getTodayReservations(stationId);
        return ResponseEntity.ok(ApiResponse.success("Today's reservations retrieved successfully", reservations));
    }

    @PostMapping("/reservations/{reservationId}/check-in")
    @Operation(summary = "Check in a reservation", description = "Check in a reservation when driver arrives")
    public ResponseEntity<ApiResponse<ReservationSummaryResponse>> checkInReservation(
            @PathVariable Long reservationId,
            @Valid @RequestBody CheckInReservationRequest request) {
        ReservationSummaryResponse response = staffReservationService.checkInReservation(reservationId, request);
        return ResponseEntity.ok(ApiResponse.success("Reservation checked in successfully", response));
    }

    @PostMapping("/reservations/{reservationId}/no-show")
    @Operation(summary = "Mark reservation as no-show", description = "Mark a reservation as no-show and create penalty transaction")
    public ResponseEntity<ApiResponse<ReservationSummaryResponse>> markAsNoShow(
            @PathVariable Long reservationId) {
        ReservationSummaryResponse response = staffReservationService.markAsNoShow(reservationId);
        return ResponseEntity.ok(ApiResponse.success("Reservation marked as no-show with penalty applied", response));
    }
}
