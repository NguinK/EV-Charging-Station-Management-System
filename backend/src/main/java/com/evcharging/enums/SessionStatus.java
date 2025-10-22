package com.evcharging.enums;

/**
 * Trạng thái phiên sạc
 */
public enum SessionStatus {
    PENDING, // Đặt chỗ nhưng chưa bắt đầu
    ACTIVE,      // Đang sạc
    COMPLETED,   // Hoàn thành
    CANCELLED,   // Đã hủy
    CHARGING, ERROR        // Lỗi
}
