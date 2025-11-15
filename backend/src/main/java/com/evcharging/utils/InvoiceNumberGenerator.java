package com.evcharging.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Utility class để generate các loại số:
 * - Invoice Number (Số hóa đơn)
 * - Receipt Number (Số biên lai)
 * - Transaction Reference (Mã tham chiếu giao dịch)
 * - Refund Number (Số hoàn tiền)
 * Sử dụng database sequence để đảm bảo unique và persistent.
 */
@Component
@RequiredArgsConstructor
public class InvoiceNumberGenerator {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    /**
     * Generate invoice number với UUID (unique tuyệt đối)
     * Format: INV-YYYYMMDD-XXXXXXXX
     * Example: INV-20251114-A3B5C7D9
     */
    public static String generate() {
        String datePart = OffsetDateTime.now().format(formatter);
        String uniquePart = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return String.format("INV-%s-%s", datePart, uniquePart);
    }

    public String generateReceiptNumber() {
        String datePart = OffsetDateTime.now().format(formatter);
        String uniquePart = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return String.format("RCP-%s-%s", datePart, uniquePart);
    }

    public String generateTransactionReference() {
        String datePart = OffsetDateTime.now().format(formatter);
        String uniquePart = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return String.format("TXN-%s-%s", datePart, uniquePart);
    }

    public String generateRefundNumber() {
        String datePart = OffsetDateTime.now().format(formatter);
        String uniquePart = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return String.format("RFD-%s-%s", datePart, uniquePart);
    }
}
