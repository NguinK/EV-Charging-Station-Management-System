package com.evcharging.entity;

import com.evcharging.enums.SubscriptionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

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

    private Double discountPercent; // Tỷ lệ giảm giá (%)

    private OffsetDateTime createdAt;

    @Column(nullable = false)
    private Double price;


    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
    }
}

