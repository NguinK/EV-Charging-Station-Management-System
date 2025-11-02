package com.evcharging.entity;

import com.evcharging.enums.*;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
@Entity
@Table(name = "invoices")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String invoiceNumber; // Số hóa đơn

    @OneToOne
    @JoinColumn(name = "transaction_id")
    private Transaction transaction;


    // Thông tin khách hàng
    private String customerName;

    // Thông tin hóa đơn
    private String stationName;
    private String pointCode;
    private Double energyConsumed;


    // Chi phí
    private Double discount;
    private Double finalAmount;


    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
