package com.evcharging.dto;

import com.evcharging.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * DTO cho tài khoản hệ thống EV Charging, bao gồm:
 * - EV Driver (người dùng xe điện)
 * - Charging Station Staff (nhân viên trạm)
 * - Admin (quản trị viên)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDTO {

    // ----------- Thông tin cơ bản -----------
    private Long userId;
    private String email;
    private String phone;
    private String fullName;
    private String password;     // chỉ dùng cho đăng ký/đăng nhập
    private Role role;           // EV_DRIVER, STATION_STAFF, ADMIN
    private String token;        // JWT token trả về sau khi đăng nhập

    // ----------- Thông tin mở rộng cho EV Driver -----------
    private String vehicleModel;       // Mẫu xe (VD: Tesla Model 3)
    private String vehiclePlate;       // Biển số xe
    private Double walletBalance;      // Số dư ví điện tử
    private List<TransactionDTO> transactions; // Lịch sử giao dịch
    private List<ChargingHistoryDTO> chargingHistory; // Lịch sử sạc

    // ----------- Thông tin mở rộng cho Station Staff -----------
    private Long stationId;            // Trạm đang làm việc
    private String stationName;        // Tên trạm

    // ----------- Thông tin quản trị (Admin) -----------
    private boolean active;            // Tài khoản đang hoạt động hay không
    private OffsetDateTime createdAt;   // Ngày tạo tài khoản
    private OffsetDateTime updatedAt;   // Lần cập nhật gần nhất
}
