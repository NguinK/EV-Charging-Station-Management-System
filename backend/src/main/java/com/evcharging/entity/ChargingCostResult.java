package com.evcharging.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ChargingCostResult {
    private double energyCost;
    private double timeCost;
    private double discount;
    private double vatAmount;
    private double finalAmount;

    // constructor, getters, setters
}