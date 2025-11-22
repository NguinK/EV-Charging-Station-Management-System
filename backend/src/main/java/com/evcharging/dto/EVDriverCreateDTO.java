package com.evcharging.dto;


import com.evcharging.enums.ConnectorType;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EVDriverCreateDTO {

    @NotBlank
    @Size(min = 2, max = 25)
    private String fullName;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Pattern(regexp = "^\\+?[0-9]{9,15}$", message = "Invalid phone number")
    private String phone;

    @NotBlank
    @Size(min = 6, message = "The password must at least has 6 characters ")
    private String password;

    // Thông tin bổ sung cho EV Driver
    @NotNull(message = "Driver license is required")
    private String driverLicense;

    @NotBlank(message = "Vehicle number is required")
    private String vehicleNumber;

    @NotBlank(message = "Vehicle type is required")
    private String vehicleType;
    @NotNull(message = "Date of birth is required")

    private LocalDate dateOfBirth;
    @NotNull(message = "Address is required")
    private String address;

    @NotNull(message = "Battery Capacity is required")
    @DecimalMin(value = "10.0", message = "Battery capacity is at least 10 kWh")
    @DecimalMax(value = "200.0", message = "Battery capacity is at most 200kWh")
    private Double batteryCapacity;

    @NotNull(message = "Car's connector type is required")
    private ConnectorType connectorType;
}
