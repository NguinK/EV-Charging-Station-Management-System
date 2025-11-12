package com.evcharging.dto.admin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChargingStationDashboardResponse {
    private Long totalStations;
    private Long onlineStations;
    private Long offlineStations;
    private Long totalPoints;
    private Long availablePoints;
    private Long occupiedPoints;
    private Long offlinePoints;
    private Long maintenancePoints;
    private Integer totalSystemPower;  // kW
    private Double utilizationRate;    // %
}