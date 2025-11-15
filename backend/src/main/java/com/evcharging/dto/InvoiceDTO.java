package com.evcharging.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Data
public class InvoiceDTO {
    // Basic info
    private Long id;
    private String invoiceNumber;
    private OffsetDateTime issuedAt;
    private String status;

    // Customer info
    private String customerName;
    private String customerPhone;
    private String customerEmail;

    // Station info
    private String stationName;
    private String pointCode;

    // Charging session info
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private Long durationMinutes;
    private Double energyConsumed;

    // Price breakdown
    private Double pricePerKwh;
    private Double pricePerMinute;
    private Double energyFee;
    private Double timeFee;
    private Double serviceFee;
    private Double reservationFee;

    // Total
    private Double subtotal;
    private Double discount;
    private Double tax;
    private Double finalAmount;

    // Payment info
    private String paymentMethod;
    private OffsetDateTime paidAt;
    private Long transactionId;
    private String processedByStaffName;
}