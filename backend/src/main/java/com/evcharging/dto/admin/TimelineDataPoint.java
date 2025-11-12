package com.evcharging.dto.admin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimelineDataPoint {
    private String period;  // "2025-10-27" hoặc "2025-10" hoặc "2025-W43"
    private Double revenue;
    private Integer sessionCount;
}