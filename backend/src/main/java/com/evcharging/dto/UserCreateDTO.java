package com.evcharging.dto;

import com.evcharging.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import jakarta.validation.constraints.NotNull;


/**
 * DTO dùng khi Admin hoặc hệ thống tạo tài khoản người dùng mới.
 * Hỗ trợ: EV_DRIVER, CS_STAFF, ADMIN
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCreateDTO {

    @NotBlank
    @Size(min = 2, max = 100)
    private String fullName;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Pattern(regexp = "^\\+?[0-9]{9,15}$", message = "Số điện thoại không hợp lệ")
    private String phone;

    @NotBlank
    @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
    private String password;

    @NotNull
    private Role role; // ADMIN, EV_DRIVER, CS_STAFF
}
