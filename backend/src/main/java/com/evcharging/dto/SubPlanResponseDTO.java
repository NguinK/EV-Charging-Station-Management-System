package com.evcharging.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.OffsetDateTime;

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
    private String name;            // Basic, VIP
    private Double discountPercent; // % giảm giá
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private OffsetDateTime createdAt;  // Ngày tạo gói
    private Double price;
}
