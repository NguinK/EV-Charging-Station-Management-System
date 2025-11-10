package com.evcharging.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

/**
 * DTO cho lịch sử sạc của người dùng (EV Driver).
 * Hiển thị trong phần "Lịch sử & phân tích cá nhân".
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChargingHistoryDTO {

    private Long historyId;        // Mã bản ghi lịch sử
    private Long userId;           // Người thực hiện sạc
    private Long sessionId;        // Phiên sạc liên quan
    private Long stationId;        // Trạm sạc
    private String stationName;    // Tên trạm sạc
    private String connectorType;  // Loại cổng sạc: CCS, CHAdeMO, AC
    private double power;          // Công suất (kW)
    private double energyUsed;     // Năng lượng đã sử dụng (kWh)
    private double cost;           // Tổng chi phí (VNĐ)
    private double socStart;       // SOC lúc bắt đầu (%)
    private double socEnd;         // SOC lúc kết thúc (%)
    private String paymentMethod;  // Hình thức thanh toán (E-Wallet, Banking, On-site)
    private String status;         // COMPLETED, FAILED, CANCELLED
    private OffsetDateTime startTime;  // Thời gian bắt đầu
    private OffsetDateTime endTime;    // Thời gian kết thúc
}
