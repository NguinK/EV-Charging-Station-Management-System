package com.evcharging.dto.staff;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChargingPointStatusUpdateRequest {
    @NotBlank(message = "Status is required")
    private String status;

    private String reason;
}
