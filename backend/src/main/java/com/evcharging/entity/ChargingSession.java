package com.evcharging.entity;

import com.evcharging.enums.PaymentMethod;
import com.evcharging.enums.SessionStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Getter
@Setter
@Table(name = "charging_sessions")
@Data
public class ChargingSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_time", nullable = false)
    private OffsetDateTime startTime;     // Thời gian bắt đầu

    @Column(name = "end_time")
    private OffsetDateTime endTime;       // Thời gian kết thúc

    @Column(name = "energy_consumed")
    private Double energyConsumed;       // kWh đã sạc

    @Column(name = "cost")
    private double cost;                 // Chi phí tạm tính

    @Column(name = "start_soc")
    private Integer startSoc;                // SOC % lúc bắt đầu

    @Column(name = "end_soc")
    private Integer endSoc;                  // SOC % lúc kết thúc

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SessionStatus status;        // PENDING, CHARGING, COMPLETED, CANCELLED

    @Column(name = "started_by_staff_id")
    private Long startedByStaffId;

    @Column(name = "ended_by_staff_id")
    private Long endedByStaffId;

    // Quan hệ với Driver
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "driver_id", nullable = false)
    private EVDriver driver;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "vehicle_id", nullable = false)
//    private Vehicle vehicle;

    // Quan hệ với Station
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    private ChargingStation station;

    // Quan hệ 1-1 với Reservation (Reservation giữ mappedBy)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    // Quan hệ 1-1 với Transaction
    @OneToOne(mappedBy = "session", cascade = CascadeType.ALL)
    private Transaction transaction;

    private OffsetDateTime lastUpdatedTime;

    //Quan hệ M-1 với ChargingPoint
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "charging_point_id", nullable = false)
    private ChargingPoint chargingPoint;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

//    @Enumerated(EnumType.STRING)
//    private PaymentMethod paymentMethod;


    public Double getEnergyDelivered() {
        return this.energyConsumed;
    }


    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
        updatedAt = OffsetDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        lastUpdatedTime = OffsetDateTime.now();
    }

}