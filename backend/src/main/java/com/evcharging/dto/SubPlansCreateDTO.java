package com.evcharging.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SubPlansCreateDTO {
    private String name;
    private String type;
    private double price;
    private Integer durationDay;

}
