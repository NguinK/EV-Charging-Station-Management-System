package com.evcharging.dto;

import com.evcharging.enums.Role;
import lombok.*;

/**
 * DTO phản hồi sau khi đăng nhập thành công.
 * Gửi thông tin token, vai trò và tên người dùng về cho client.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDTO {
    private String token;
    private String fullName;
    private String email;
    private Role role;    // DRIVER / STAFF / ADMIN
}
