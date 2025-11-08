package com.evcharging.dto.staff;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StartSessionRequest {
    private Long reservationId; // optional for walk-ins

    @NotNull(message = "Station ID is required")
    private Long stationId;

    @NotNull(message = "Charging point ID is required")
    private Long chargingPointId;

    @NotNull(message = "Driver ID is required")
    private Long driverId;
}
