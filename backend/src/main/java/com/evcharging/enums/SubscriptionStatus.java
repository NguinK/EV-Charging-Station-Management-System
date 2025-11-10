package com.evcharging.enums;

public enum SubscriptionStatus {
    ACTIVE,      // Đang hoạt động
    EXPIRED,     // Đã hết hạn
    CANCELLED,   // Đã hủy
    SUSPENDED,   // Tạm ngưng (do thanh toán thất bại, vi phạm,...)
    PENDING      // Chờ kích hoạt (sau khi thanh toán)
}