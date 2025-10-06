package com.evcharging.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO phản hồi cho thông tin gói thuê bao (Subscription Plan).
 * Dùng cho Admin, Driver hoặc Staff để hiển thị danh sách gói.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubPlanResponseDTO {
    private Long id;
    private String name;           // Tên gói (ví dụ: "Gói VIP", "Gói Basic")
    private String type;           // Loại gói: PREPAID / POSTPAID / MEMBERSHIP
    private Double price;          // Giá gói (VNĐ hoặc USD)
    private Integer durationDays;  // Thời hạn (ngày)

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime createdAt;  // Ngày tạo gói

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime updatedAt;  // Ngày cập nhật gần nhất
}
