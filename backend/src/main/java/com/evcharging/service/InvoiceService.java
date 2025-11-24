package com.evcharging.service;

import com.evcharging.dto.InvoiceDTO;
import com.evcharging.entity.*;
import com.evcharging.enums.TransactionStatus;
import com.evcharging.enums.TransactionType;
import com.evcharging.exception.BusinessException;
import com.evcharging.exception.ResourceNotFoundException;
import com.evcharging.repository.InvoiceRepository;
import com.evcharging.utils.InvoiceNumberGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.sql.Driver;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceNumberGenerator invoiceNumberGenerator;
    private final InvoicePdfGenerator invoicePdfGenerator;
    private final PricingService pricingService;

    //Tạo invoice từ transaction đã thanh toán thành công
    @Transactional
    public Invoice createInvoice(Transaction transaction) {
        // 1. Validate transaction
        validateTransactionForInvoice(transaction);

        // 2. Check if invoice already exists
        if (invoiceRepository.existsByTransactionId(transaction.getId())) {
            throw new BusinessException("Invoice already exists for this transaction");
        }

        // 3. Generate invoice number (professional way)
        String invoiceNumber = invoiceNumberGenerator.generate();

        // 4. Create invoice entity
        Invoice invoice = new Invoice();
        invoice.setTransaction(transaction);
        invoice.setInvoiceNumber(invoiceNumber);
        invoice.setCreatedAt(OffsetDateTime.now());
        invoice.setIssuedAt(OffsetDateTime.now());
        invoice.setStatus("ISSUED"); // ISSUED, CANCELLED, REFUNDED

        // 5. Extract customer info
        EVDriver driver = transaction.getDriver();
        if (driver != null) {
            invoice.setCustomerName(driver.getFullName());
            invoice.setCustomerPhone(driver.getPhone());
            invoice.setCustomerEmail(driver.getAccount() != null
                    ? driver.getAccount().getEmail() : null);
        }

        // 6. Extract charging session info
        ChargingSession session = transaction.getSession();
        if (session != null) {
            // Station info
            if (session.getStation() != null) {
                invoice.setStationName(session.getStation().getName());
                invoice.setStationId(session.getStation().getId());
            }

            // Charging point info
            ChargingPoint point = session.getChargingPoint();
            if (point != null) {
                invoice.setPointCode(point.getPointCode());
                invoice.setPointId(point.getId());
            }

            // Session timing
            invoice.setStartTime(session.getStartTime());
            invoice.setEndTime(session.getEndTime());

            // Energy consumed
            invoice.setEnergyConsumed(session.getEnergyConsumed());

            // Calculate duration
            if (session.getStartTime() != null && session.getEndTime() != null) {
                long minutes = Duration.between(
                        session.getStartTime(),
                        session.getEndTime()
                ).toMinutes();
                invoice.setDurationMinutes(minutes);
            }
        }

        invoice.setPaymentMethod(transaction.getPaymentMethod() != null
                ? transaction.getPaymentMethod().name() : null);
        invoice.setPaidAt(transaction.getPaidAt());
        invoice.setFinalAmount(transaction.getAmount().doubleValue());
        invoice = invoiceRepository.save(invoice);

        log.info("Invoice created successfully: {} for transaction: {}",
                invoiceNumber, transaction.getId());

        return invoice;
    }

    private void validateTransactionForInvoice(Transaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction cannot be null");
        }

        if (transaction.getStatus() != TransactionStatus.SUCCESS) {
            throw new BusinessException("Can only create invoice for successful transactions");
        }

        if (transaction.getType() != TransactionType.PAYMENT) {
            throw new BusinessException("Can only create invoice for charging payment transactions");
        }

        if (transaction.getSession() == null) {
            throw new BusinessException("Transaction must have an associated charging session");
        }

        if (transaction.getDriver() == null) {
            throw new BusinessException("Transaction must have an associated driver");
        }
    }

    @Transactional(readOnly = true)
    public byte[] generateInvoicePdf(Long invoiceId) {
        Invoice invoice = findById(invoiceId);

        // Map entity to DTO
        InvoiceDTO dto = mapToDTO(invoice);

        // Generate PDF
        try {
            byte[] pdfBytes = invoicePdfGenerator.generate(dto);
            log.info("PDF generated for invoice: {}", invoice.getInvoiceNumber());
            return pdfBytes;
        } catch (IOException e) {
            log.error("Failed to generate PDF for invoice: {}", invoiceId, e);
            throw new BusinessException("Failed to generate invoice PDF: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public byte[] generateInvoicePdfByNumber(String invoiceNumber) {
        Invoice invoice = findByInvoiceNumber(invoiceNumber);
        return generateInvoicePdf(invoice.getId());
    }

    @Transactional(readOnly = true)
    public Invoice findById(Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public Invoice findByInvoiceNumber(String invoiceNumber) {
        return invoiceRepository.findByInvoiceNumber(invoiceNumber)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Invoice not found with number: " + invoiceNumber));
    }

    @Transactional(readOnly = true)
    public Invoice findByTransactionId(Long transactionId) {
        return invoiceRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Invoice not found for transaction: " + transactionId));
    }

    @Transactional(readOnly = true)
    public List<Invoice> findByDriverId(Long driverId) {
        return invoiceRepository.findByDriverIdOrderByCreatedAtDesc(driverId);
    }

    @Transactional(readOnly = true)
    public List<Invoice> findByStationAndDateRange(
            Long stationId,
            OffsetDateTime startDate,
            OffsetDateTime endDate) {
        return invoiceRepository.findByStationIdAndCreatedAtBetween(
                stationId, startDate, endDate);
    }

    @Transactional
    public Invoice cancelInvoice(Long invoiceId, String reason) {
        Invoice invoice = findById(invoiceId);

        if ("CANCELLED".equals(invoice.getStatus())) {
            throw new BusinessException("Invoice is already cancelled");
        }

        if ("REFUNDED".equals(invoice.getStatus())) {
            throw new BusinessException("Cannot cancel a refunded invoice");
        }

        invoice.setStatus("CANCELLED");
        invoice = invoiceRepository.save(invoice);

        log.info("Invoice cancelled: {} - Reason: {}", invoice.getInvoiceNumber(), reason);

        return invoice;
    }

    private InvoiceDTO mapToDTO(Invoice invoice) {
        InvoiceDTO dto = new InvoiceDTO();

        // Basic info
        dto.setId(invoice.getId());
        dto.setInvoiceNumber(invoice.getInvoiceNumber());
        dto.setIssuedAt(invoice.getIssuedAt());
        dto.setStatus(invoice.getStatus());

        // Customer info
        dto.setCustomerName(invoice.getCustomerName());
        dto.setCustomerPhone(invoice.getCustomerPhone());
        dto.setCustomerEmail(invoice.getCustomerEmail());

        // Station info
        dto.setStationName(invoice.getStationName());
        dto.setPointCode(invoice.getPointCode());

        // Session info
        dto.setStartTime(invoice.getStartTime());
        dto.setEndTime(invoice.getEndTime());
        dto.setDurationMinutes(invoice.getDurationMinutes());
        dto.setEnergyConsumed(invoice.getEnergyConsumed());

        // Price breakdown
        dto.setPricePerKwh(invoice.getPricePerKwh());
        dto.setPricePerMinute(invoice.getPricePerMinute());
        dto.setEnergyFee(invoice.getEnergyFee());
        dto.setTimeFee(invoice.getTimeFee());
        dto.setServiceFee(invoice.getServiceFee());
        dto.setReservationFee(invoice.getReservationFee());

        // Total
        dto.setSubtotal(invoice.getSubtotal());
        dto.setDiscount(invoice.getDiscount());
        dto.setTax(invoice.getTax());
        invoice.setFinalAmount(invoice.getSubtotal() + invoice.getTax() - (invoice.getDiscount() != null ? invoice.getDiscount() : 0));

        // Payment info
        dto.setPaymentMethod(invoice.getPaymentMethod());
        dto.setPaidAt(invoice.getPaidAt());

        if (invoice.getTransaction() != null) {
            dto.setTransactionId(invoice.getTransaction().getId());

            if (invoice.getTransaction().getProcessedByStaffId() != null) {
                // TODO: Load staff name from database
                dto.setProcessedByStaffName("Staff #" +
                        invoice.getTransaction().getProcessedByStaffId());
            }
        }

        return dto;
    }

    @Transactional(readOnly = true)
    public Double getTotalRevenueByStation(Long stationId, OffsetDateTime startDate, OffsetDateTime endDate) {
        return invoiceRepository.sumFinalAmountByStationAndDateRange(stationId, startDate, endDate);
    }

}