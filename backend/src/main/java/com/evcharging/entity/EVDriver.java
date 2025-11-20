package com.evcharging.entity;

import com.evcharging.enums.ConnectorType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;


@Getter
@Setter
@Entity
@Table(name = "users")
public class EVDriver {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;
    // liên kết tới bảng account
    private String fullName;
    private String phone;
    private String driverLicense;
    private String vehicleNumber;
    private String vehicleType;
    private LocalDate dateOfBirth;
    private String address;

    /**
     * Total battery capacity of the vehicle in kilowatt-hours (kWh), NOT a percentage.
     * This represents the maximum energy storage capacity of the EV battery.
     */
    @Positive
    @Column(name = "battery_capacity_kwh")
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
    @Enumerated(EnumType.STRING)
    @Column(name = "connector_type")
    private ConnectorType connectorType;
}
