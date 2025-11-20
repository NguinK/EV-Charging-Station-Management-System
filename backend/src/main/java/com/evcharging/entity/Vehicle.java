//package com.evcharging.entity;
//
//import com.evcharging.enums.ConnectorType;
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//
//import java.time.OffsetDateTime;
//
//@Entity
//@Table(name = "vehicles")
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//public class Vehicle {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne
//    @JoinColumn(name = "account_id")
//    private Account account;
//
//    private String manufacturer; // Tesla, VinFast, BYD, etc.
//    private String model;
//    private String licensePlate;
//    private Double batteryCapacity; // kWh
//
//    @Enumerated(EnumType.STRING)
//    private ConnectorType connectorType;
//
//    private OffsetDateTime createdAt;
//
//    @PrePersist
//    protected void onCreate() {
//        createdAt = OffsetDateTime.now();
//    }
//}
