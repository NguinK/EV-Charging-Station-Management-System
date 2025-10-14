package com.evcharging.entity;


import com.evcharging.enums.PaymentMethod;
import com.evcharging.enums.TransactionStatus;
import com.evcharging.enums.TransactionType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime timestamp;     // Thời gian giao dịch
    private double amount;               // Số tiền
    private String currency;             // VND, USD...

    @Enumerated(EnumType.STRING)
    private TransactionType type;        // TOP_UP, CHARGING_PAYMENT, SUBSCRIPTION, REFUND

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod; // EWALLET, BANKING, CASH, CREDIT_CARD

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;    // SUCCESS, FAILED, PENDING

    private String invoiceNumber;        // Mã hóa đơn điện tử

    // Quan hệ
    @ManyToOne
    @JoinColumn(name = "driver_id", nullable = false)
    private EVDriver driver;             // Tài xế thực hiện giao dịch

    @OneToOne
    @JoinColumn(name = "session_id")
    private ChargingSession session;     // Phiên sạc liên quan (nếu có)
}