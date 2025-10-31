package com.evcharging.dto;

import com.evcharging.enums.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO hiển thị thông tin quản trị viên (Admin)
 * Dùng cho API trả về danh sách Admin, chi tiết tài khoản, hoặc dashboard quản trị.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    private String email;
    private String phone;
    private String fullName;
    private Role role;              // Thường là ADMIN, nhưng giúp tái sử dụng DTO cho SuperAdmin hoặc SubAdmin
    private boolean active;         // Tình trạng tài khoản
}
