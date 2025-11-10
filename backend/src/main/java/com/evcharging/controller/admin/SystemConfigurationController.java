package com.evcharging.controller.admin;

import com.evcharging.dto.ApiResponse;
import com.evcharging.service.SystemConfigurationService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/configurations")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class SystemConfigurationController {

    private final SystemConfigurationService configService;

    @PutMapping("/{configKey}")
    @Operation(summary = "Update configuration value")
    public ResponseEntity<ApiResponse<Void>> updateConfiguration(
            @PathVariable String configKey,
            @RequestParam String newValue,
            @RequestParam(required = false) String updatedBy) {

        configService.updateConfigValue(configKey, newValue, updatedBy);
        return ResponseEntity.ok(ApiResponse.success("Configuration updated successfully", null));
    }

    @GetMapping("/{configKey}")
    @Operation(summary = "Get configuration value")
    public ResponseEntity<ApiResponse<String>> getConfiguration(@PathVariable String configKey) {
        String value = configService.getConfigValue(configKey, null);
        return ResponseEntity.ok(ApiResponse.success(value));
    }

}
