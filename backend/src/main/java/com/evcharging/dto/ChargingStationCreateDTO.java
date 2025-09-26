package com.evcharging.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ChargingStationCreateDTO {
    private String name;
    private String location;
    private String status; // ONLINE/OFFLINE
    private Integer totalPorts;

}
