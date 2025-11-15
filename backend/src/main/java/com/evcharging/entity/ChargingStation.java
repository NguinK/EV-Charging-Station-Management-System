package com.evcharging.entity;

import com.evcharging.enums.StationStatus;
import jakarta.persistence.*;
import lombok.Data;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "charging_stations")
@Data
public class ChargingStation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, length = 500)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StationStatus status;

    @Column(name = "total_points")  // Tên cột trong DB vẫn giữ nguyên
    private Integer totalPoints;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
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

    private String operator;
    private double latitude;
    private double longitude;

    public String getAddress() {
        return this.location;
    }

    public void setAddress(String address) {
        this.location = address;
    }

    public List<ChargingPoint> getChargingPoints() {
        return this.points;
    }

    public String getOperatorName() {
        return this.operator;
    }

    public void setOperatorName(String operatorName) {
        this.operator = operatorName;
    }

    public String getContactPhone() {
        return this.operator;
    }

    public void setContactPhone(String contactPhone) {
        this.operator = contactPhone;
    }

    @OneToMany(mappedBy = "station", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChargingPoint> points = new ArrayList<>();

    @OneToMany(mappedBy = "station", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChargingSession> sessions = new ArrayList<>();
}