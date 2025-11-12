package com.evcharging.dto.admin;

import com.evcharging.enums.ChargingPointStatus;
import com.evcharging.enums.ChargingSpeed;
import com.evcharging.enums.ConnectorType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChargingPointResponse {
    private Long id;
    private String pointCode;
    private ConnectorType connectorType;
    private Integer maxPower;
    private ChargingSpeed speed;
    private ChargingPointStatus status;
    private Double pricePerKwh;
    private Double pricePerMinute;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    // Thông tin trạm (không bao gồm toàn bộ station object)
    private Long stationId;
    private String stationName;
    private String location;
}