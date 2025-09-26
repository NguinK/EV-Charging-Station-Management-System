package com.evcharging.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SubPlanResponseDTO {
    private Long id;
    private String name;
    private String type;
    private Double price;
    private Integer durationDays;

}
