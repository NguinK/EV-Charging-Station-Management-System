package com.evcharging.dto;

import jakarta.validation.constraints.*;
import lombok.*;

/**
 * DTO dùng khi Admin tạo gói thuê bao mới.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubPlanCreateDTO {

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Discount percent is required")
    @DecimalMin(value = "0.0", message = "Discount must be >= 0")
    @DecimalMax(value = "100.0", message = "Discount must be <= 100")
    private Double discountPercent;

    @NotNull(message = "Price is required")
    private Double price;




}
