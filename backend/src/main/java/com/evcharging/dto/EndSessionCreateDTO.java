package com.evcharging.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EndSessionCreateDTO {
    /**
     * Battery state of charge in percentage (0-100) at the end of charging, NOT kWh.
     */
    @Min(value = 0, message = "End SoC must be between 0 and 100")
    @Max(value = 100, message = "End SoC must be between 0 and 100")
    private int endSoc;

    /**
     * Energy consumed during charging in kilowatt-hours (kWh).
     */
    private double energy;

    private double cost;
}
