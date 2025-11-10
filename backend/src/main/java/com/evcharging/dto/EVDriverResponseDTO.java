package com.evcharging.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO phản hồi thông tin người dùng (Admin, EV Driver, CS Staff)
 * dùng cho API GET /users hoặc sau khi đăng ký/tạo tài khoản.
 */
@Getter
@Setter
@AllArgsConstructor
@Data
public class EVDriverResponseDTO {
    private Long id;
    private String fullName;
    private String email;
    private String vehicleNumber;

}
