package com.evcharging.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

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


    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
    }
}
