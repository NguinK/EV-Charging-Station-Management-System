package com.evcharging.entity;

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
    private String driverLicense;
    private String vehicleNumber;
    private String vehicleType;
    private LocalDate dateOfBirth;
    private String address;
}
