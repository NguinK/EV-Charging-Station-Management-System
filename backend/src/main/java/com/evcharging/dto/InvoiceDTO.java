package com.evcharging.dto;

import lombok.Getter;
import lombok.Setter;

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