package com.evcharging.dto.staff;

import lombok.Data;

@Data
public class StationSummaryResponse {
    private Long id;
    private String name;
    private String location;
    private String status;
    private Integer totalChargers;
    private Integer availableChargers;
    private Integer inUseChargers;
}
