package com.evcharging.dto.admin;

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
public class ChargingStationUsageResponse {
    private Long stationId;
    private String stationName;
    private Integer totalSessions;
    private Integer totalPoints;
    private Double averageSessionsPerDay;
    private Double utilizationRate;  // %
    private List<PeakHourData> peakHours;
    private OffsetDateTime periodStart;
    private OffsetDateTime periodEnd;
    private Double totalRevenue;
    private Double totalEnergyDelivered;  // kWh
    private Integer activePoints;
    private Integer maintenancePoints;
}

