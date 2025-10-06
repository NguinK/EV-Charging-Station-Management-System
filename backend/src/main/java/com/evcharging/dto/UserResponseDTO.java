package com.evcharging.dto;

import com.evcharging.enums.Role;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO phản hồi thông tin người dùng (Admin, EV Driver, CS Staff)
 * dùng cho API GET /users hoặc sau khi đăng ký/tạo tài khoản.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDTO {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private Role role;  // ADMIN / EV_DRIVER / CS_STAFF

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime updatedAt;
}
