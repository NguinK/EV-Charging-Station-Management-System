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
public class ChargingStationOperationResponse {
    private Boolean success;
    private Long stationId;
    private String stationName;
    private StationStatus oldStatus;
    private StationStatus newStatus;
    private ChargingStationOperation operation;
    private String message;
    private OffsetDateTime timestamp;
}