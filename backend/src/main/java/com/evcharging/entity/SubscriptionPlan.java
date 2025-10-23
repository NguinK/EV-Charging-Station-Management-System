package com.evcharging.entity;

import com.evcharging.enums.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
@Entity
@Table(name = "subscription_plans")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // Basic, Premium, VIP

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private Double monthlyFee; // Phí hàng tháng (VND)

    private Double discountRate; // Tỷ lệ giảm giá (%)

    private Integer freeMinutesPerMonth; // Số phút miễn phí/tháng
    private Double freeKwhPerMonth; // Số kWh miễn phí/tháng

    @Enumerated(EnumType.STRING)
    private PlanStatus status;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

