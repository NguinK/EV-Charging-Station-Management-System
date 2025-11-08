package com.evcharging.dto.staff;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CheckInReservationRequest {
    @NotNull(message = "Charger ID is required if not pre-assigned")
    private Long chargerId;
}
