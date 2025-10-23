package com.evcharging.entity;

import com.evcharging.enums.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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

    @OneToOne
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @Column(nullable = false, unique = true)
    private String invoiceNumber; // Số hóa đơn

    @Column(nullable = false)
    private LocalDateTime issueDate; // Ngày phát hành

    // Thông tin khách hàng
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String customerAddress;
    private String taxCode; // Mã số thuế (nếu có)

    // Thông tin hóa đơn
    private String stationName;
    private String pointCode;
    private LocalDateTime chargingStartTime;
    private LocalDateTime chargingEndTime;
    private Double energyDelivered;
    private Integer duration;

    // Chi phí
    private Double energyCost;
    private Double timeCost;
    private Double discount;
    private Double totalAmount;

    // VAT
    private Double vatRate; // Thuế VAT (%)
    private Double vatAmount;
    private Double finalAmount; // Tổng cộng sau VAT

    @Column(length = 1000)
    private String notes;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
