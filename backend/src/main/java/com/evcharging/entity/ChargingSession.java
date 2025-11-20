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

    /**
     * Battery state of charge in percentage (0-100) at the start of the charging session, NOT kWh.
     */
    @Column(name = "start_soc")
    private Integer startSoc;

    /**
     * Battery state of charge in percentage (0-100) at the end of the charging session, NOT kWh.
     */
    @Column(name = "end_soc")
    private Integer endSoc;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SessionStatus status;        // PENDING, CHARGING, COMPLETED, CANCELLED

    @Column(name = "started_by_staff_id")
    private Long startedByStaffId;

    @Column(name = "ended_by_staff_id")
    private Long endedByStaffId;

    // Quan hệ với Driver
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private EVDriver driver;

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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "charging_point_id", nullable = false)
    private ChargingPoint chargingPoint;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

//    @Enumerated(EnumType.STRING)
//    private PaymentMethod paymentMethod;

    @Column(name = "total_cost")
    private Double totalCost;

    public Double getEnergyDelivered() {
        return this.energyConsumed;
    }

    public Double getTotalCost() {
        if (this.totalCost != null && this.totalCost > 0) {
            return this.totalCost;
        }
        if (this.cost != 0) {
            return this.cost;
        }
        return 0.0;
    }

    public void setTotalCost(Double cost) {
        this.totalCost = cost;
        this.cost = cost;
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