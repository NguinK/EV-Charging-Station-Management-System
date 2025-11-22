package com.evcharging.controller.admin;

import com.evcharging.dto.ApiResponse;
import com.evcharging.dto.admin.CreateStaffRequest;
import com.evcharging.dto.admin.StaffResponse;
import com.evcharging.service.admin.AdminStaffService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//Admin controller for managing staff users
@Slf4j
@RestController
@RequestMapping("/api/admin/staff")
@RequiredArgsConstructor
@Tag(name = "Admin Staff Management", description = "APIs for managing staff users (Admin only)")
public class AdminStaffController {

    private final AdminStaffService adminStaffService;

    @Operation(summary = "Create new staff user", description = "Create a new staff account and assign to charging stations")
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<StaffResponse>> createStaff(
            @Valid @RequestBody CreateStaffRequest request) {

        log.info("POST /api/admin/staff - Creating staff with email: {}", request.getEmail());

        try {
            StaffResponse response = adminStaffService.createStaff(request);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Staff user created successfully", response));
        } catch (IllegalArgumentException e) {
            log.error("Validation error creating staff: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Error creating staff", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to create staff user"));
        }
    }

    @Operation(summary = "Get all staff users", description = "Get paginated list of all staff users")
    @GetMapping("/getAll")
    public ResponseEntity<ApiResponse<Page<StaffResponse>>> getAllStaff(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("GET /api/admin/staff - page: {}, size: {}", page, size);

        try {
            Page<StaffResponse> staff = adminStaffService.getAllStaff(page, size);
            return ResponseEntity.ok(ApiResponse.success("Staff users retrieved successfully", staff));
        } catch (Exception e) {
            log.error("Error retrieving staff users", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve staff users"));
        }
    }

    @Operation(summary = "Get staff by ID", description = "Get detailed information about a specific staff user")
    @GetMapping("/{staffId}")
    public ResponseEntity<ApiResponse<StaffResponse>> getStaffById(@PathVariable Long staffId) {
        log.info("GET /api/admin/staff/{}", staffId);

        try {
            StaffResponse response = adminStaffService.getStaffById(staffId);
            return ResponseEntity.ok(ApiResponse.success("Staff details retrieved successfully", response));
        } catch (IllegalArgumentException e) {
            log.error("Staff not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Error retrieving staff details", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve staff details"));
        }
    }

    @Operation(summary = "Update staff station assignments", description = "Update the charging stations assigned to a staff member")
    @PutMapping("/{staffId}/assignments")
    public ResponseEntity<ApiResponse<StaffResponse>> updateStaffAssignments(
            @PathVariable Long staffId,
            @RequestBody List<Long> stationIds) {

        log.info("PUT /api/admin/staff/{}/assignments - {} stations", staffId, stationIds.size());

        try {
            StaffResponse response = adminStaffService.updateStaffAssignments(staffId, stationIds);
            return ResponseEntity.ok(ApiResponse.success("Staff assignments updated successfully", response));
        } catch (IllegalArgumentException e) {
            log.error("Validation error: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Error updating staff assignments", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to update staff assignments"));
        }
    }

    @Operation(summary = "Deactivate staff user", description = "Deactivate a staff account and remove all station assignments")
    @DeleteMapping("/{staffId}")
    public ResponseEntity<ApiResponse<Void>> deactivateStaff(@PathVariable Long staffId) {
        log.info("DELETE /api/admin/staff/{}", staffId);

        try {
            adminStaffService.deactivateStaff(staffId);
            return ResponseEntity.ok(ApiResponse.success("Staff user deactivated successfully", null));
        } catch (IllegalArgumentException e) {
            log.error("Staff not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Error deactivating staff", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to deactivate staff user"));
        }
    }
}