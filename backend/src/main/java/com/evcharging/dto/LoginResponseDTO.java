package com.evcharging.dto;

import com.evcharging.enums.Role;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

/**
 * DTO phản hồi sau khi đăng nhập thành công.
 * Gửi thông tin token, vai trò và tên người dùng về cho client.
 */
@Data
@Builder
public class LoginResponseDTO {
    private String token;
    private String fullName;
    private String email;
    private Role role;   // EV_DRIVER / CS_STAFF / ADMIN
    private Instant expiresAt; // optional

    public LoginResponseDTO() {
    }

    public LoginResponseDTO(String token, String fullName, String email, Role role, Instant expiresAt) {
        this.token = token;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.expiresAt = expiresAt;
    }


}