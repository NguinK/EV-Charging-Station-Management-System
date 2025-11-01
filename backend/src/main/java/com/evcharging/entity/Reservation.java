package com.evcharging.entity;

import com.evcharging.enums.ConnectorType;
import com.evcharging.enums.ReservationStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
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

    // Driver đặt chỗ
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private EVDriver driver;

    // Trạm sạc được đặt
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    private ChargingStation station;

    private Double holdingFee;

    @Enumerated(EnumType.STRING)
    @Column(name = "connector_type", nullable = false, length = 50)
    private ConnectorType connectorType; // CCS, CHAdeMO, AC

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status; // PENDING, CONFIRMED, CANCELLED, EXPIRED

    @Column(name = "start_time", nullable = false)
    private OffsetDateTime startTime;

    @Column(name = "expire_time", nullable = false)
    private OffsetDateTime expireTime;

    // Quan hệ 1-1 với ChargingSession
    @OneToOne(mappedBy = "reservation", cascade = CascadeType.ALL)
    private ChargingSession chargingSession;

    @ManyToOne
    private ChargingPoint chargingPoint;
}