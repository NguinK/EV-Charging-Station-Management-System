package com.evcharging.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO cho giao dịch thanh toán của EV Driver.
 * Dùng để hiển thị trong lịch sử giao dịch hoặc chi tiết ví điện tử.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {

    private Long id;                   // Mã giao dịch
    private Long userId;               // Người thực hiện giao dịch
    private Long chargingSessionId;    // Phiên sạc liên quan (nếu có)
    private double amount;             // Số tiền thanh toán (VNĐ)
    private String paymentMethod;      // Phương thức thanh toán: E-Wallet, Banking, On-site, Subscription
    private String paymentType;        // Theo kWh / theo thời gian / gói thuê bao
    private LocalDateTime transactionTime; // Thời điểm thanh toán
    private String status;             // SUCCESS, FAILED, PENDING
    private String description;        // Ghi chú thêm (VD: “Thanh toán phiên sạc tại trạm FPT”)
}

