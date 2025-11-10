package com.evcharging.entity;


import com.evcharging.enums.PaymentMethod;
import com.evcharging.enums.TransactionStatus;
import com.evcharging.enums.TransactionType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@JsonFormat(shape = JsonFormat.Shape.STRING)
@Entity
@Data
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private OffsetDateTime timestamp;     // Thời gian giao dịch

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;               // Số tiền

    @Column(nullable = false, length = 10)
    private String currency = "VND";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TransactionType type;        // TOP_UP, CHARGING_PAYMENT, SUBSCRIPTION, REFUND

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod; // EWALLET, BANKING, CASH, CREDIT_CARD

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransactionStatus status;    // SUCCESS, FAILED, PENDING

    @Column(name = "invoice_number", length = 100)
    private String invoiceNumber;        // Mã hóa đơn điện tử

    @Column(name = "paid_at")
    private OffsetDateTime paidAt;   // Thời điểm thanh toán thành công

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "charging_session_id")
    private ChargingSession chargingSession;

    @Column(length = 500)
    private String description;

    // Quan hệ
    @ManyToOne
    @JoinColumn(name = "driver_id", nullable = false)
    private EVDriver driver;             // Tài xế thực hiện giao dịch

    @OneToOne
    @JoinColumn(name = "session_id")
    private ChargingSession session;     // Phiên sạc liên quan (nếu có)

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
        updatedAt = OffsetDateTime.now();
        if (timestamp == null) {
            timestamp = OffsetDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}