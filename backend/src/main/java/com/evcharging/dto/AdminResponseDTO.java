package com.evcharging.dto;

import com.evcharging.enums.Role;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO phản hồi khi lấy thông tin Admin (GET API)
 * Dùng trong trang quản trị hoặc dashboard admin.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminResponseDTO {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private Role role; // ví dụ: ADMIN, SUPER_ADMIN
    private boolean active;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime createdAt;
}
