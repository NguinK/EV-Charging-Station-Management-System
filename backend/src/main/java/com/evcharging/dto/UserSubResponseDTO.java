package com.evcharging.dto;

import com.evcharging.enums.SubscriptionStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserSubResponseDTO {
    private Long id;
    private Long accountId;
    private String planName;
    private Double discountPercent;
    private OffsetDateTime startDate;
    private OffsetDateTime endDate;
    private SubscriptionStatus status;
    private Boolean autoRenew;


}
