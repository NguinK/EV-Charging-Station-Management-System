package com.evcharging.dto;

import com.evcharging.enums.ChargingPointStatus;
import com.evcharging.enums.ChargingSpeed;
import com.evcharging.enums.ConnectorType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChargingPointCreateDTO {
    private Long stationId;
    private String pointCode;
    private ConnectorType connectorType;
    private Integer maxPower;
    private ChargingSpeed speed;
    private ChargingPointStatus status;
    private Double pricePerKwh;
    private Double pricePerMinute;
}