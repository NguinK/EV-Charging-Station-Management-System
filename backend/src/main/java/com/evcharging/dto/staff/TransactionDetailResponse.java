package com.evcharging.dto.staff;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Data
public class TransactionDetailResponse {
    private Long id;
    private BigDecimal amount;
    private String currency;
    private String type;
    private String status;
    private String paymentMethod;
    private String paymentNotes;
    private String invoiceNumber;
    private OffsetDateTime timestamp;
    private OffsetDateTime paidAt;
    private Long processedByStaffId;
    private String processedByStaffName;

    // Session/Reservation info
    private Long chargingSessionId;
    private Long reservationId;
    private Long driverId;
    private String driverName;
}
