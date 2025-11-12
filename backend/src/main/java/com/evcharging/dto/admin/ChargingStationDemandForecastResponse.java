package com.evcharging.dto.admin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChargingStationDemandForecastResponse {
    private Long stationId;
    private String stationName;
    private Map<YearMonth, Integer> historicalData;
    private Double growthRate;  // % per month
    private List<MonthlyForecast> forecasts;
    private Integer currentCapacity;  // số charging points
    private Double capacityUtilization;  // %
    private List<String> recommendations;
    private OffsetDateTime generatedAt;
}