package com.evcharging.enums;

/**
 * Trạng thái thanh toán
 */
public enum PaymentStatus {
    PENDING,        // Chờ thanh toán
    PROCESSING,     // Đang xử lý
    COMPLETED,      // Thành công
    FAILED,         // Thất bại
    REFUNDED,       // Đã hoàn tiền
    CANCELLED       // Đã hủy
}