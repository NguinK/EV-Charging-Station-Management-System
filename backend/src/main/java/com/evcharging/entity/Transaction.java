package com.evcharging.entity;


import com.evcharging.enums.PaymentMethod;
import com.evcharging.enums.TransactionStatus;
import com.evcharging.enums.TransactionType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
@JsonFormat(shape = JsonFormat.Shape.STRING)
@Entity
@Data
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime timestamp;     // Thời gian giao dịch
    private double amount;               // Số tiền

    @Enumerated(EnumType.STRING)
    private TransactionType type;        // TOP_UP, CHARGING_PAYMENT, SUBSCRIPTION, REFUND

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod; // EWALLET, BANKING, CASH, CREDIT_CARD

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;    // SUCCESS, FAILED, PENDING

    private String invoiceNumber;        // Mã hóa đơn điện tử

    private LocalDateTime paidAt;   // Thời điểm thanh toán thành công

    private String currency;

    // Quan hệ
    @ManyToOne
    @JoinColumn(name = "driver_id", nullable = false)
    private EVDriver driver;             // Tài xế thực hiện giao dịch

    @OneToOne
    @JoinColumn(name = "session_id")
    private ChargingSession session;     // Phiên sạc liên quan (nếu có)
}