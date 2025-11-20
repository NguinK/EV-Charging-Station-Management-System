package com.evcharging.dto;

import com.evcharging.enums.SessionStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChargingSessionDTO {
    private Long id;
    private String stationName;
    private String driverName;
    private String pointCode;
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private int startSoc;
    private int endSoc;
    private double energyConsumed;
    private double cost;
    private SessionStatus status;
    private Long transactionId;
    private String paymentUrl;
    private Long stationId;
    private Long driverId;
    private Long pointId;
}
