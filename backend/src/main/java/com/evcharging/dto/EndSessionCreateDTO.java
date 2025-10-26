package com.evcharging.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EndSessionCreateDTO {
    private int endSoc;
    private double energy;
    private double cost;
}
