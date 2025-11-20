package com.evcharging.dto;

import com.evcharging.enums.StationStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * DTO dùng để tạo mới trạm sạc (Charging Station)
 * Dành cho Admin trong trang quản trị hệ thống.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChargingStationCreateDTO {

    @NotBlank(message = "Station name is required")
    private String name;

    @NotBlank(message = "Location is required")
    private String location;

    @NotNull(message = "Status is required")// ACTIVE/INACTIVE
    private StationStatus status;

    @NotNull(message = "Total ports is required")
    private Integer totalPoints;
}
