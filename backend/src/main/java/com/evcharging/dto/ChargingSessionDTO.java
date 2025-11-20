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

    /**
     * Battery state of charge in percentage (0-100) at the start of charging, NOT kWh.
     */
    private int startSoc;

    /**
     * Battery state of charge in percentage (0-100) at the end of charging, NOT kWh.
     */
    private int endSoc;

    /**
     * Energy consumed during the charging session in kilowatt-hours (kWh).
     */
    private double energyConsumed;

    private double cost;
    private SessionStatus status;
    private Long transactionId;
    private String paymentUrl;

}
