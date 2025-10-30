package com.evcharging.dto;


import com.evcharging.enums.ConnectorType;
import com.evcharging.enums.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@AllArgsConstructor
public class ReservationResponseDTO {
    private Long id;
    private String stationName;
    private ConnectorType connectorType;
    private ReservationStatus status;
    private LocalDateTime startTime;
    private LocalDateTime expireTime;
    private Long chargingPointId;
    private Long stationId;
    private Double holdingFee;
}
