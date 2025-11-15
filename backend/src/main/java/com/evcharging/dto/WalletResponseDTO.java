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
public class WalletResponseDTO {
    private Long id;
    private Double balance;
    private String status;
    private Long accountId;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}