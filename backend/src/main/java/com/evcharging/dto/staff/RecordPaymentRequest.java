package com.evcharging.dto.staff;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RecordPaymentRequest {
    @NotNull(message = "Payment method is required")
    private String paymentMethod; //CASH, EWALLET, BANKING

    private String paymentNotes;

    private String receiptNumber; //Số biên lai
}
