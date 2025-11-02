//package com.evcharging.entity;
//
//import com.evcharging.enums.*;
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import java.time.LocalDateTime;
//
///**
// * Entity thanh toán
// */
//@Entity
//@Table(name = "payments")
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//
//public class Payment {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @OneToOne
//    @JoinColumn(name = "session_id", nullable = false)
//    private ChargingSession session;
//
//    @ManyToOne
//    @JoinColumn(name = "account_id", nullable = false)
//    private Account account;
//
//    @Column(nullable = false)
//    private Double amount; // Tổng số tiền (VND)
//
//    private Double energyCost; // Chi phí theo kWh
//    private Double timeCost; // Chi phí theo thời gian
//    private Double discount; // Giảm giá (nếu có)
//    private Double finalAmount; // Số tiền sau giảm giá
//
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private PaymentMethod method;
//
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private PaymentStatus status;
//
//    @Column(unique = true)
//    private String transactionId; // Mã giao dịch từ payment gateway
//
//    @OneToOne
//    @JoinColumn(name = "invoice_id")
//    private Invoice invoice;
//
//    private LocalDateTime paymentTime;
//    private LocalDateTime createdAt;
//
//    @PrePersist
//    protected void onCreate() {
//        createdAt = LocalDateTime.now();
//    }
//
//
//}
