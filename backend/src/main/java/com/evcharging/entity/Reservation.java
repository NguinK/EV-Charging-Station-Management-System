package com.evcharging.entity;

import com.evcharging.enums.ConnectorType;
import com.evcharging.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Data
@Getter
@Setter
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "charging_point_id")
    private ChargingPoint chargingPoint;

    // Driver đặt chỗ
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver", nullable = false)
    private EVDriver driver;

    // Trạm sạc được đặt
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    private ChargingStation station;

    @Enumerated(EnumType.STRING)
    @Column(name = "connector_type", nullable = false, length = 50)
    private ConnectorType connectorType; // CCS, CHAdeMO, AC

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ReservationStatus status; // PENDING, CONFIRMED, CANCELLED, EXPIRED

    @Column(name = "holding_fee", precision = 10, scale = 2)
    private BigDecimal holdingFee;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @Column(name = "start_time", nullable = false)
    private OffsetDateTime startTime;

    @Column(name = "expire_time", nullable = false)
    private OffsetDateTime expireTime;

    // Quan hệ 1-1 với ChargingSession
    @OneToOne(mappedBy = "reservation", cascade = CascadeType.ALL)
    private ChargingSession chargingSession;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
        updatedAt = OffsetDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}