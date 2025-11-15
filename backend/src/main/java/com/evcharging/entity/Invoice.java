package com.evcharging.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.OffsetDateTime;

@Entity
@Table(name = "invoices")
@Data
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String invoiceNumber; // Số hóa đơn

    @OneToOne
    @JoinColumn(name = "transaction_id")
    private Transaction transaction;

    //Customer info
    private String customerName;
    private String customerEmail;
    private String customerPhone;

    //Station/Point info
    private String stationName;
    private Long stationId;
    private Long pointId;
    private String pointCode;

    //Time tracking
    private OffsetDateTime issuedAt;
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private Long durationMinutes;
    private Double energyConsumed;

    //Pricing details
    private Double pricePerKwh;
    private Double pricePerMinute;
    private Double energyFee;
    private Double timeFee;
    private Double serviceFee;
    private Double reservationFee;
    private Double subtotal;
    private Double discount;
    private Double tax;
    private Double finalAmount;

    //Payment
    private String paymentMethod;
    private OffsetDateTime paidAt;
    private String status;

    //Cancellation
    private OffsetDateTime cancelledAt;
    @Column(length = 500)
    private String cancelReason;

    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
        if (issuedAt == null) {
            issuedAt = OffsetDateTime.now();
        }
    }
}
