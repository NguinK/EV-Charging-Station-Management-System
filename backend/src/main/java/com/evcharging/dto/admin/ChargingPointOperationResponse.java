package com.evcharging.dto.admin;
import com.evcharging.enums.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChargingPointOperationResponse {
    private Boolean success;
    private Long pointId;
    private String pointCode;
    private String stationName;
    private ChargingPointStatus oldStatus;
    private ChargingPointStatus newStatus;
    private ChargingPointOperation operation;
    private String message;
    private OffsetDateTime timestamp;
}