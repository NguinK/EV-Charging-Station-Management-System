package com.evcharging.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * DTO dùng khi Admin tạo gói thuê bao mới.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubPlansCreateDTO {

    @NotBlank
    private String name;       // Tên gói, ví dụ: "Gói VIP", "Gói Basic"

    @NotBlank
    private String type;       // Loại gói: PREPAID / POSTPAID / MEMBERSHIP

    @NotNull
    @Min(0)
    private Double price;      // Giá gói (VNĐ hoặc USD)

    @NotNull
    @Min(1)
    private Integer durationDays; // Số ngày hiệu lực của gói
}
