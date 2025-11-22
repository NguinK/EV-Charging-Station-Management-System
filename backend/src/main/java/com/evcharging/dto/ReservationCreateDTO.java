package com.evcharging.dto;

import com.evcharging.enums.ConnectorType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Data
@Getter
@Setter
@AllArgsConstructor
public class ReservationCreateDTO {
    @NotNull(message = "StationId is required")
    private Long stationId;

    @NotNull(message = "Connector type is required")
    private ConnectorType connectorType;

    @NotNull(message = "End time is required")
    @Future(message = "End time must be in the future")
    private OffsetDateTime endTime;

}