package com.evcharging.dto;

import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO cho giao dịch thanh toán của EV Driver.
 * Dùng để hiển thị trong lịch sử giao dịch hoặc chi tiết ví điện tử.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TransactionDTO {
    private Long id;                       // Mã giao dịch
    private Long driverId;                 // Người thực hiện giao dịch
    private String driverName;             // Tên tài xế
    private Long sessionId;                // Phiên sạc liên quan (nếu có)

    private double amount;                 // Số tiền thanh toán (VNĐ)
    private String currency;               // Loại tiền tệ (VD: VND, USD)
    private String paymentMethod;          // Phương thức thanh toán: E-Wallet, Banking, On-site, Subscription
    private String paymentType;            // Loại giao dịch: PAYMENT, REFUND, SUBSCRIPTION
    private LocalDateTime transactionTime; // Thời điểm thanh toán
    private String status;                 // SUCCESS, FAILED, PENDING
    private String invoiceNumber;          // Mã hóa đơn
    private LocalDateTime paidAt;          // Thời điểm thanh toán thành công
    private String description;            // Ghi chú thêm (VD: “Thanh toán phiên sạc tại trạm FPT”)

}

