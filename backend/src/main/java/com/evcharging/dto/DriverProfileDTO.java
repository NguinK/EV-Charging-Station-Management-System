package com.evcharging.dto;

import com.evcharging.enums.ConnectorType;
import jakarta.validation.constraints.Positive;
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
    private String password;
    private Long driverId;

    /**
     * Total battery capacity of the vehicle in kilowatt-hours (kWh).
     */
    @Positive
    private Double batteryCapacityKwh;

    /**
     * Vehicle manufacturer (e.g., Tesla, VinFast, BYD).
     */
    private String manufacturer;

    /**
     * Vehicle model (e.g., Model 3, VF8).
     */
    private String model;

    /**
     * Vehicle license plate number.
     */
    private String licensePlate;

    /**
     * Type of charging connector supported by the vehicle.
     */
    private ConnectorType connectorType;
}
