package com.evcharging.dto.admin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyForecast {
    private String month;  // "2025-11"
    private Integer predictedSessions;
    private Double confidence;  // 0.0 - 1.0
}