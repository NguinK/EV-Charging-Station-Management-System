package com.evcharging.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
public class InvoiceDTO {
    private Long id;
    private String invoiceNumber;
    private String customerName;
    private String stationName;
    private String pointCode;
    private Double finalAmount;
}