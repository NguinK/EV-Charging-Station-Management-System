package com.evcharging.dto.admin;

import com.evcharging.entity.ChargingPoint;
import com.evcharging.enums.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChargingStationDetailResponse {
    private Long id;
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
    private StationStatus status;
    private String operatorName;
    private String contactPhone;
    private Integer totalPoints;
    private Integer availablePoints;
    private Integer occupiedPoints;
    private Integer totalPower;
    private Double utilizationRate;
    private List<ChargingPoint> points;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}