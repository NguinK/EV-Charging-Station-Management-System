package com.evcharging.entity;

import com.evcharging.enums.ConnectorType;
import com.evcharging.enums.ChargingSpeed;
import com.evcharging.enums.PointStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "charging_points")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChargingPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "station_id", nullable = false)
    private ChargingStation station;

    @Column(nullable = false)
    private String pointCode; // Mã điểm sạc (ví dụ: CP-001, CP-002)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConnectorType connectorType; // CCS, CHADEMO, AC_TYPE2

    private Integer maxPower; // Công suất tối đa (kW)

    @Enumerated(EnumType.STRING)
    private ChargingSpeed speed; // SLOW, FAST, ULTRA_FAST

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PointStatus status; // AVAILABLE, OCCUPIED, OFFLINE, RESERVED, MAINTENANCE

    @Column(nullable = false)
    private Double pricePerKwh; // Giá theo kWh (VND)

    @Column(nullable = false)
    private Double pricePerMinute; // Giá theo phút (VND)

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}