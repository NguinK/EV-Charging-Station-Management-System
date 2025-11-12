package com.evcharging.dto.admin;
import com.evcharging.enums.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateChargingPointRequest {
    private String pointCode;
    private ConnectorType connectorType;
    private Integer maxPower;
    private Double pricePerKwh;
    private Double pricePerMinute;
}