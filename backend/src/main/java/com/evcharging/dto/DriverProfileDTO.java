package com.evcharging.dto;
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
    private String phone;
    private String email;
}