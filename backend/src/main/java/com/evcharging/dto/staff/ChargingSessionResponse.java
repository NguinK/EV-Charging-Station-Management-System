package com.evcharging.dto.staff;

import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
public class ChargingSessionResponse {
    private Long id;
    private Long reservationId;
    private Long stationId;
    private String stationName;
    private Long chargerId;
    private String chargerCode;
    private Long driverId;
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private double energyUsedKwh;
    private String status;
    private Long startedByStaffId;
    private Long endedByStaffId;
}
