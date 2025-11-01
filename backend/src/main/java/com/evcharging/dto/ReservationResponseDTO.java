package com.evcharging.dto;


import com.evcharging.enums.ConnectorType;
import com.evcharging.enums.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Data
@Getter
@Setter
@AllArgsConstructor
public class ReservationResponseDTO {
    private Long reservationId;
    private String stationName;
    private ConnectorType connectorType;
    private ReservationStatus status;
    private OffsetDateTime startTime;
    private OffsetDateTime expireTime;
    private Long chargingPointId;
    private Long stationId;
    private Double holdingFee;
}
