package com.evcharging.dto;

import com.evcharging.enums.ConnectorType;
import lombok.Data;

import java.time.LocalDate;

@Data
public class DriverProfileDTO {
    private String fullName;
    private LocalDate dateOfBirth;
    private String address;
    private String driverLicense;
    private String vehicleNumber;
    private String vehicleType;
    private Double batteryCapacity;
    private ConnectorType connectorType;
    private String phone;
    private String email;
    private String password;
    private Long driverId;
}
