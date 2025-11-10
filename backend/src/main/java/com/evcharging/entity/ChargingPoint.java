package com.evcharging.entity;

import com.evcharging.enums.ChargingPointStatus;
import com.evcharging.enums.ChargingSpeed;
import com.evcharging.enums.ConnectorType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Table(name = "charging_point", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"station_id", "code"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChargingPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "point_code", unique = true, nullable = false)
    private String pointCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    private ChargingStation station;

    @Column(nullable = false, length = 50)
    private String code; // Mã điểm sạc (ví dụ: CP-001, CP-002)

    //    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ConnectorType connectorType; // CCS, CHADEMO, AC_TYPE2

    private Integer maxPower; // Công suất tối đa (kW)

    @Enumerated(EnumType.STRING)
    private ChargingSpeed speed; // SLOW, FAST, ULTRA_FAST

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ChargingPointStatus status; // AVAILABLE, OCCUPIED, OFFLINE, RESERVED, MAINTENANCE

    @Column(nullable = false)
    private Double pricePerKwh; // Giá theo kWh (VND)

    @Column(nullable = false)
    private Double pricePerMinute; // Giá theo phút (VND)

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

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