package com.evcharging.dto;

import com.evcharging.enums.ChargingPointStatus;
import com.evcharging.enums.ChargingSpeed;
import com.evcharging.enums.ConnectorType;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChargingPointCreateDTO {
    @NotNull(message = "StationId is required")
    private Long stationId;

    @NotBlank(message = "Point code is required")
    @Size(max = 50, message = "Point code must not exceed 50 characters")
    private String pointCode;

    @NotNull(message = "Connector type is required")
    private ConnectorType connectorType;

    @NotNull(message = "Max power is required")
    @Min(value = 1, message = "Max power must be greater than 0")
    private Integer maxPower;

    @NotNull(message = "Charging speed is required")
    private ChargingSpeed speed;

    @NotNull(message = "Status is required")
    private ChargingPointStatus status;

    @NotNull(message = "Price per kWh is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price per kWh must be greater than 0")
    private Double pricePerKwh;

    @NotNull(message = "Price per minute is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Price per minute must be >= 0")
    private Double pricePerMinute;

}