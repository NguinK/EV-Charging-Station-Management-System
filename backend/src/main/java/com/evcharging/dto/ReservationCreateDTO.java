package com.evcharging.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@AllArgsConstructor
public class ReservationCreateDTO {
    private Long stationId;          // ID trạm sạc muốn đặt
    private String connectorType;    // CCS, CHAdeMO, AC
    private LocalDateTime startTime;}