package com.evcharging.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletTransactionResponseDTO {
    private Long id;
    private Long walletId;
    private String type;
    private Double amount;
    private Double balanceBefore;
    private Double balanceAfter;
    private String description;
    private OffsetDateTime createdAt;
}