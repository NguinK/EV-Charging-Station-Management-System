package com.evcharging.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ChargingStationResponseDTO {
    private Long id;
    private String name;
    private String location;
    private String status;
    private Integer totalPorts;
}
