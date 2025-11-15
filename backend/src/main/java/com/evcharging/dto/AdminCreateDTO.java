package com.evcharging.dto;

import com.evcharging.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO dùng để tạo mới tài khoản quản trị (Admin).
 * Áp dụng cho chức năng "Quản lý người dùng & gói dịch vụ" trong hệ thống EV Charging.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminCreateDTO {

    @NotBlank
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;

    @NotBlank
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank
    @Size(min = 6, message = "Password must have at least 6 characters")
    private String password;

    @Pattern(regexp = "^\\+?[0-9]{9,15}$", message = "Phone number must be valid")
    private String phone;

    @Builder.Default
    private Role role = Role.ADMIN; // Mặc định là ADMIN khi tạo qua DTO

    @Builder.Default
    private boolean active = true;   // Cho phép set trạng thái tài khoản (true = kích hoạt)
}
