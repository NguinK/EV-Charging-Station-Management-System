package com.evcharging.dto;


import com.evcharging.enums.ConnectorType;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;


/**
 * DTO dùng khi Admin hoặc hệ thống tạo tài khoản người dùng mới.
 * Hỗ trợ: EV_DRIVER, CS_STAFF, ADMIN
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EVDriverCreateDTO {

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

    // Thông tin bổ sung cho EV Driver
    private String driverLicense;

    @NotBlank(message = "Biển số xe là bắt buộc")
    private String vehicleNumber;

    @NotBlank(message = "Loại xe là bắt buộc")
    private String vehicleType;
    private LocalDate dateOfBirth;
    private String address;

    @NotNull(message = "Dung lượng pin là bắt buộc")
    @DecimalMin(value = "10.0", message = "Dung lượng pin tối thiểu 10 kWh")
    @DecimalMax(value = "200.0", message = "Dung lượng pin tối đa 200 kWh")
    private Double batteryCapacity;

    @NotNull(message = "Loại cổng sạc là bắt buộc")
    private ConnectorType connectorType;
}
