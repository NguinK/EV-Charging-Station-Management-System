package com.evcharging.dto.staff;

import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
public class ReservationSummaryResponse {
    private Long id;
    private Long driverId;
    private String driverName;
    private String driverPhone;
    private Long stationId;
    private String stationName;
    private Long chargerId;
    private String chargerCode;
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private String status;
    private BigDecimal holdFee;
}
