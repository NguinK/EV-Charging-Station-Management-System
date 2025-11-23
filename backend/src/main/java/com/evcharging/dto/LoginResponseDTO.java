package com.evcharging.dto;

import com.evcharging.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * DTO phản hồi sau khi đăng nhập thành công.
 * Gửi thông tin token, vai trò và tên người dùng về cho client.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {
    private String token;
    private String fullName;
    private String email;
    private Role role;   // EV_DRIVER / CS_STAFF / ADMIN
    private Instant expiresAt; // optional
    private Long driverId;
    private Long adminId;
    private Long staffId;
    private Long accountId;

}