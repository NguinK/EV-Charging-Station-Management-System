package com.evcharging.dto.staff;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class StopSessionRequest {
    @NotNull(message = "Energy used is required")
    @Positive(message = "Energy used must be positive")
    private double energyUsedKwh;

    private String paymentMethod;
    private String paymentNotes;

}
