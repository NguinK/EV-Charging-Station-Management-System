package com.evcharging.dto.admin;

import com.evcharging.entity.ChargingPoint;
import com.evcharging.enums.*;
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
public class RevenueTimelineResponse {
    private OffsetDateTime startDate;
    private OffsetDateTime endDate;
    private TimeGranularity granularity;
    private List<TimelineDataPoint> dataPoints;
    private Double totalRevenue;
    private Integer totalSessions;
    private Double averageRevenuePerPeriod;
}