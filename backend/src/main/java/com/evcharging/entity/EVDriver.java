package com.evcharging.entity;

import com.evcharging.enums.ConnectorType;
import jakarta.persistence.*;
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
    private LocalDate dateOfBirth;
    private String address;

    private String vehicleNumber;
    private String vehicleType;

    @Column(name = "battery_capacity")
    private Double batteryCapacity;

    @Enumerated(EnumType.STRING)
    @Column(name = "connector_type")
    private ConnectorType connectorType;
}