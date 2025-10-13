package com.evcharging.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class ChargingStation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;            // Tên trạm (VD: Trạm Vincom Thủ Đức)
    private String location;        // Địa chỉ hoặc tọa độ GPS
    private String status;          // ONLINE, OFFLINE, MAINTENANCE
    private int totalPoints;        // Tổng số điểm sạc
    private String operator;        // Đơn vị vận hành (VD: Xanh SM)

    @OneToMany(mappedBy = "station", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChargingSession> sessions = new ArrayList<>();
}