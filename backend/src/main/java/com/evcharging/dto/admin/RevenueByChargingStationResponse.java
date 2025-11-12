package com.evcharging.dto.admin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RevenueByChargingStationResponse {
    private Long stationId;
    private String stationName;
    private String address;
    private Double totalRevenue;
    private Double totalEnergy;  // kWh
    private Integer totalSessions;
    private Double averageRevenuePerSession;
}