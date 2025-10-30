package com.evcharging.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
public class InvoiceDTO {
    private Long id;
    private String invoiceNumber;
    private LocalDateTime issueDate;

    private String customerName;
    private String taxCode;

    private String stationName;
    private String pointCode;
    private LocalDateTime chargingStartTime;
    private LocalDateTime chargingEndTime;
    private Double energyDelivered;
    private Integer duration;

    private Double energyCost;
    private Double timeCost;
    private Double discount;
    private Double totalAmount;

    private Double vatRate;
    private Double vatAmount;
    private Double finalAmount;

    private String notes;
}