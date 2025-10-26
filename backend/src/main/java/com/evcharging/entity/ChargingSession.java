package com.evcharging.entity;

import com.evcharging.enums.SessionStatus;
import jakarta.persistence.*;
import lombok.Data;


import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "charging_sessions")
public class ChargingSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;     // Thời gian bắt đầu

    @Column(name = "end_time")
    private LocalDateTime endTime;       // Thời gian kết thúc

    @Column(name = "energy_consumed")
    private double energyConsumed;       // kWh đã sạc

    @Column(name = "cost")
    private double cost;                 // Chi phí tạm tính

    @Column(name = "start_soc")
    private int startSoc;                // SOC % lúc bắt đầu

    @Column(name = "end_soc")
    private int endSoc;                  // SOC % lúc kết thúc

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SessionStatus status;        // PENDING, CHARGING, COMPLETED, CANCELLED

    // Quan hệ với Driver
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private EVDriver driver;

    // Quan hệ với Station
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    private ChargingStation station;

    // Quan hệ 1-1 với Reservation (Reservation giữ mappedBy)
    @OneToOne
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    // Quan hệ 1-1 với Transaction
    @OneToOne(mappedBy = "session", cascade = CascadeType.ALL)
    private Transaction transaction;
    private LocalDateTime lastUpdatedTime;

    //Quan hệ M-1 với ChargingPoint
    @ManyToOne
    @JoinColumn(name = "charging_point_id")
    private ChargingPoint chargingPoint;

}