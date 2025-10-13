package com.evcharging.entity;

import com.evcharging.enums.SessionStatus;
import jakarta.persistence.*;


import java.time.LocalDateTime;

@Entity
public class ChargingSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime startTime;     // Thời gian bắt đầu
    private LocalDateTime endTime;       // Thời gian kết thúc
    private double energyConsumed;       // kWh đã sạc
    private double cost;                 // Chi phí tạm tính
    private int startSoc;                // SOC % lúc bắt đầu
    private int endSoc;                  // SOC % lúc kết thúc

    @Enumerated(EnumType.STRING)
    private SessionStatus status;      // PENDING, CHARGING, COMPLETED, CANCELLED

    // Quan hệ
    @ManyToOne
    @JoinColumn(name = "driver_id", nullable = false)
    private EVDriver driver;             // Tài xế tham gia phiên sạc

    @ManyToOne
    @JoinColumn(name = "station_id", nullable = false)
    private ChargingStation station;     // Trạm sạc (nếu bạn có class ChargingStation)

    @OneToOne(mappedBy = "session", cascade = CascadeType.ALL)
    private Transaction transaction;     // Giao dịch thanh toán liên quan
}