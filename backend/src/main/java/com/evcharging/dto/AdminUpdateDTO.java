package com.evcharging.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO dùng để cập nhật thông tin quản trị viên (Admin)
 * Áp dụng cho chức năng "Cập nhật tài khoản quản trị" trong hệ thống EV Charging.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUpdateDTO {

    @NotBlank
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;

    @Pattern(regexp = "^\\+?[0-9]{9,15}$", message = "Phone number must be valid")
    private String phone;

    private boolean active;  // Cho phép SuperAdmin khóa/mở tài khoản khác

    @Size(min = 6, message = "Password must have at least 6 characters")
    private String newPassword;  // Tuỳ chọn: chỉ dùng khi Admin muốn đổi mật khẩu
}
