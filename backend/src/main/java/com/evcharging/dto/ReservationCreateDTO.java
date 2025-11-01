package com.evcharging.dto;

import com.evcharging.enums.ConnectorType;
import com.fasterxml.jackson.annotation.JsonFormat;
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
public class ReservationCreateDTO {
    private Long stationId;          // ID trạm sạc muốn đặt
    private ConnectorType connectorType;// CCS, CHAdeMO, AC
    private OffsetDateTime endTime;}