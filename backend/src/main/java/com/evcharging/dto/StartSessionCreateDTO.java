package com.evcharging.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StartSessionCreateDTO {
    private Long reservationId;

    /**
     * Battery state of charge in percentage (0-100) at the start of charging, NOT kWh.
     */
    @Min(value = 0, message = "Start SoC must be between 0 and 100")
    @Max(value = 100, message = "Start SoC must be between 0 and 100")
    private int startSoc;
}
