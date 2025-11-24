package com.evcharging.service.admin;

import com.evcharging.dto.admin.*;
import com.evcharging.entity.*;
import com.evcharging.enums.TimeGranularity;
import com.evcharging.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.time.OffsetDateTime;
import java.util.stream.Collectors;

/**
 * Service báo cáo và thống kê cho Admin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminReportService {

    private final PaymentRepository paymentRepo;
    private final ChargingSessionRepository sessionRepo;
    private final ChargingStationRepository stationRepo;
    private final ChargingPointRepository pointRepo;  // ⭐ Thêm dependency

    /**
     * Báo cáo doanh thu theo trạm
     */
    public List<RevenueByChargingStationResponse> getRevenueByStation(OffsetDateTime startDate, OffsetDateTime endDate) {
        log.info("Getting revenue by station: {} to {}", startDate, endDate);

        List<ChargingStation> stations = stationRepo.findAll();

        return stations.stream()
                .map(station -> {
                    List<ChargingSession> sessions = sessionRepo.findByStationAndDateRange(
                            station.getId(), startDate, endDate);

                    double totalRevenue = sessions.stream()
                            .mapToDouble(ChargingSession::getCost)
                            .sum();

                    double totalEnergy = sessions.stream()
                            .mapToDouble(ChargingSession::getEnergyDelivered)
                            .sum();

                    int totalSessions = sessions.size();

                    return RevenueByChargingStationResponse.builder()
                            .stationId(station.getId())
                            .stationName(station.getName())
                            .address(station.getAddress())
                            .totalRevenue(totalRevenue)
                            .totalEnergy(totalEnergy)
                            .totalSessions(totalSessions)
                            .averageRevenuePerSession(totalSessions > 0 ? totalRevenue / totalSessions : 0)
                            .build();
                })
                .sorted((a, b) -> Double.compare(b.getTotalRevenue(), a.getTotalRevenue()))
                .collect(Collectors.toList());
    }

    /**
     * Báo cáo doanh thu theo thời gian (ngày/tuần/tháng)
     */
    public RevenueTimelineResponse getRevenueTimeline(OffsetDateTime startDate,
                                                      OffsetDateTime endDate,
                                                      TimeGranularity granularity) {
        log.info("Getting revenue timeline: {} to {}, granularity: {}",
                startDate, endDate, granularity);

        List<ChargingSession> sessions = sessionRepo.findByAccountIdAndDateRange(
                null, startDate, endDate); // null = all accounts

        Map<String, Double> revenueByPeriod = new TreeMap<>();
        Map<String, Integer> sessionsByPeriod = new TreeMap<>();

        for (ChargingSession session : sessions) {
            String period = formatPeriod(session.getStartTime(), granularity);

            revenueByPeriod.merge(period, session.getCost(), Double::sum);
            sessionsByPeriod.merge(period, 1, Integer::sum);
        }

        List<TimelineDataPoint> dataPoints = revenueByPeriod.entrySet().stream()
                .map(entry -> TimelineDataPoint.builder()
                        .period(entry.getKey())
                        .revenue(entry.getValue())
                        .sessionCount(sessionsByPeriod.getOrDefault(entry.getKey(), 0))
                        .build())
                .collect(Collectors.toList());

        double totalRevenue = dataPoints.stream()
                .mapToDouble(TimelineDataPoint::getRevenue)
                .sum();

        int totalSessions = dataPoints.stream()
                .mapToInt(TimelineDataPoint::getSessionCount)
                .sum();

        return RevenueTimelineResponse.builder()
                .startDate(startDate)
                .endDate(endDate)
                .granularity(granularity)
                .dataPoints(dataPoints)
                .totalRevenue(totalRevenue)
                .totalSessions(totalSessions)
                .averageRevenuePerPeriod(!dataPoints.isEmpty() ? totalRevenue / dataPoints.size() : 0)
                .build();
    }

    /**
     * Báo cáo tần suất sử dụng trạm
     */
    public List<ChargingStationUsageResponse> getStationUsage(OffsetDateTime startDate, OffsetDateTime endDate) {
        log.info("Getting station usage report");

        List<ChargingStation> stations = stationRepo.findAll();

        return stations.stream()
                .map(station -> {
                    List<ChargingSession> sessions = sessionRepo.findByStationAndDateRange(
                            station.getId(), startDate, endDate);

                    // Tính giờ cao điểm
                    Map<Integer, Long> sessionsByHour = sessions.stream()
                            .collect(Collectors.groupingBy(
                                    s -> s.getStartTime().getHour(),
                                    Collectors.counting()
                            ));

                    List<PeakHourData> peakHours = sessionsByHour.entrySet().stream()
                            .map(e -> new PeakHourData(e.getKey(), e.getValue().intValue()))
                            .sorted((a, b) -> Integer.compare(b.getCount(), a.getCount()))
                            .limit(5)
                            .collect(Collectors.toList());

                    // Tính tỷ lệ sử dụng trung bình
                    long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
                    int totalPoints = pointRepo.findByStationId(station.getId()).size();

                    double utilizationRate = (sessions.size() * 100.0) /
                            (days * totalPoints * 24); // Giả sử mỗi session trung bình 1 giờ

                    return ChargingStationUsageResponse.builder()
                            .stationId(station.getId())
                            .stationName(station.getName())
                            .totalSessions(sessions.size())
                            .totalPoints(totalPoints)
                            .averageSessionsPerDay(sessions.size() / (double) days)
                            .utilizationRate(Math.min(utilizationRate, 100))
                            .peakHours(peakHours)
                            .build();
                })
                .sorted((a, b) -> Integer.compare(b.getTotalSessions(), a.getTotalSessions()))
                .collect(Collectors.toList());
    }

    /**
     * ⭐ AI dự báo nhu cầu sử dụng trạm sạc
     */
    public ChargingStationDemandForecastResponse forecastDemand(Long stationId) {
        log.info("Forecasting demand for station: {}", stationId);

        ChargingStation station = stationRepo.findById(stationId)
                .orElseThrow(() -> new RuntimeException("Station not found"));

        // Lấy dữ liệu 3 tháng gần nhất
        OffsetDateTime endDate = OffsetDateTime.now();
        OffsetDateTime startDate = endDate.minusMonths(3);

        List<ChargingSession> historicalSessions = sessionRepo.findByStationAndDateRange(
                stationId, startDate, endDate);

        // Phân tích xu hướng
        Map<YearMonth, Integer> sessionsByMonth = historicalSessions.stream()
                .collect(Collectors.groupingBy(
                        s -> YearMonth.from(s.getStartTime()),
                        Collectors.summingInt(s -> 1)
                ));

        // Tính growth rate
        List<Integer> monthlyCounts = new ArrayList<>(sessionsByMonth.values());
        double growthRate = calculateGrowthRate(monthlyCounts);

        // Dự báo 3 tháng tới
        int currentMonthSessions = monthlyCounts.get(monthlyCounts.size() - 1);
        List<MonthlyForecast> forecasts = new ArrayList<>();

        for (int i = 1; i <= 3; i++) {
            YearMonth futureMonth = YearMonth.now().plusMonths(i);
            int forecastedSessions = (int) (currentMonthSessions * Math.pow(1 + growthRate, i));

            forecasts.add(MonthlyForecast.builder()
                    .month(futureMonth.toString())
                    .predictedSessions(forecastedSessions)
                    .confidence(calculateConfidence(monthlyCounts))
                    .build());
        }

        // Đánh giá capacity và đưa ra khuyến nghị
        int totalPoints = pointRepo.findByStationId(stationId).size();
        int maxCapacityPerMonth = totalPoints * 30 * 24; // 30 ngày * 24h

        double capacityUtilization = (currentMonthSessions * 100.0) / maxCapacityPerMonth;

        List<String> recommendations = generateRecommendations(
                capacityUtilization, growthRate, forecasts.get(2).getPredictedSessions(), totalPoints);

        return ChargingStationDemandForecastResponse.builder()
                .stationId(stationId)
                .stationName(station.getName())
                .historicalData(sessionsByMonth)
                .growthRate(growthRate)
                .forecasts(forecasts)
                .currentCapacity(totalPoints)
                .capacityUtilization(capacityUtilization)
                .recommendations(recommendations)
                .generatedAt(OffsetDateTime.now())
                .build();
    }

    // ========== HELPER METHODS ==========

    private String formatPeriod(OffsetDateTime dateTime, TimeGranularity granularity) {
        switch (granularity) {
            case DAILY:
                return dateTime.toOffsetTime().toString();
            case WEEKLY:
                return String.format("%d-W%02d",
                        dateTime.getYear(),
                        dateTime.get(java.time.temporal.WeekFields.ISO.weekOfYear()));
            case MONTHLY:
                return YearMonth.from(dateTime).toString();
            default:
                return dateTime.toLocalDate().toString();
        }
    }

    private double calculateGrowthRate(List<Integer> data) {
        if (data.size() < 2) return 0.0;

        double sum = 0;
        for (int i = 1; i < data.size(); i++) {
            double rate = (data.get(i) - data.get(i-1)) / (double) data.get(i-1);
            sum += rate;
        }

        return sum / (data.size() - 1);
    }

    private double calculateConfidence(List<Integer> data) {
        if (data.size() < 3) return 0.5;

        // Tính variance để đánh giá độ tin cậy
        double mean = data.stream().mapToInt(Integer::intValue).average().orElse(0);
        double variance = data.stream()
                .mapToDouble(v -> Math.pow(v - mean, 2))
                .average()
                .orElse(0);

        // Confidence giảm khi variance cao
        double confidence = 1.0 - Math.min(variance / (mean * mean), 0.5);
        return confidence;
    }

    private List<String> generateRecommendations(double utilization, double growthRate,
                                                 int futureSessionPrediction, int currentPoints) {
        List<String> recommendations = new ArrayList<>();

        if (utilization > 80) {
            recommendations.add("⚠️ URGENT: Capacity utilization is over 80%. " +
                    "Consider adding more charging points immediately.");
            int recommendedPoints = (int) Math.ceil(currentPoints * 0.5);
            recommendations.add(String.format("Recommend adding %d charging points", recommendedPoints));
        } else if (utilization > 60) {
            recommendations.add("⚡ Capacity utilization is at " + String.format("%.1f%%", utilization) +
                    ". Plan for expansion in the next quarter.");
        }

        if (growthRate > 0.15) {
            recommendations.add("📈 Strong growth rate detected (" +
                    String.format("%.1f%%", growthRate * 100) + " per month). " +
                    "Prepare for infrastructure upgrade.");
        } else if (growthRate > 0.05) {
            recommendations.add("📊 Moderate growth detected. Monitor usage trends.");
        }

        if (recommendations.isEmpty()) {
            recommendations.add("✅ Current capacity is adequate. Continue monitoring.");
        }

        return recommendations;
    }
}
