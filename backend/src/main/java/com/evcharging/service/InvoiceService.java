package com.evcharging.service;

import com.evcharging.entity.*;
import com.evcharging.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepo;
    private static final double VAT_RATE = 10.0; // 10% VAT

    /**
     * Tạo hóa đơn điện tử từ payment
     */
    @Transactional
    public Invoice generateInvoice(Payment payment) {
        log.info("Generating invoice for payment: {}", payment.getId());

        // Kiểm tra đã có invoice chưa
        if (invoiceRepo.findByPaymentId(payment.getId()).isPresent()) {
            log.warn("Invoice already exists for payment: {}", payment.getId());
            return invoiceRepo.findByPaymentId(payment.getId()).get();
        }

        ChargingSession session = payment.getSession();
        Account account = payment.getAccount();
        ChargingPoint point = session.getChargingPoint();

        // Tính VAT
        double vatAmount = payment.getFinalAmount() * (VAT_RATE / 100);
        double finalAmount = payment.getFinalAmount() + vatAmount;

        // Tạo invoice
        Invoice invoice = new Invoice();
        invoice.setPayment(payment);
        invoice.setInvoiceNumber(payment.getInvoiceNumber());
        invoice.setIssueDate(LocalDateTime.now());

        // Thông tin khách hàng

        invoice.setCustomerEmail(account.getEmail());
        invoice.setCustomerPhone(account.getPhone());

        // Thông tin trạm sạc
        invoice.setStationName(point.getStation().getName());
        invoice.setPointCode(point.getPointCode());
        invoice.setChargingStartTime(session.getStartTime());
        invoice.setChargingEndTime(session.getEndTime());
        invoice.setEnergyDelivered(session.getEnergyDelivered());
        invoice.setDuration(session.getDuration());

        // Chi phí
        invoice.setEnergyCost(payment.getEnergyCost());
        invoice.setTimeCost(payment.getTimeCost());
        invoice.setDiscount(payment.getDiscount());
        invoice.setTotalAmount(payment.getAmount());

        // VAT
        invoice.setVatRate(VAT_RATE);
        invoice.setVatAmount(vatAmount);
        invoice.setFinalAmount(finalAmount);

        invoice.setNotes("Thank you for using our EV charging service!");

        invoice = invoiceRepo.save(invoice);

        log.info("Invoice generated: {}", invoice.getInvoiceNumber());

        return invoice;
    }

    /**
     * Lấy invoice theo ID
     */
    public Invoice getInvoice(Long invoiceId) {
        return invoiceRepo.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
    }

    /**
     * Lấy invoice theo số hóa đơn
     */
    public Invoice getInvoiceByNumber(String invoiceNumber) {
        return invoiceRepo.findByInvoiceNumber(invoiceNumber)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
    }

    /**
     * Lấy invoice theo payment
     */
    public Invoice getInvoiceByPayment(Long paymentId) {
        return invoiceRepo.findByPaymentId(paymentId)
                .orElseThrow(() -> new RuntimeException("Invoice not found for this payment"));
    }

    /**
     * Export invoice as PDF (placeholder)
     */
    public byte[] exportInvoicePDF(Long invoiceId) {
        Invoice invoice = getInvoice(invoiceId);

        // TODO: Implement PDF generation using library like iText or Apache PDFBox
        log.info("Exporting invoice {} to PDF", invoice.getInvoiceNumber());

        return new byte[0]; // Placeholder
    }

    /**
     * Generate HTML invoice for email
     */
    public String generateInvoiceHTML(Long invoiceId) {
        Invoice invoice = getInvoice(invoiceId);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        return String.format("""
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; }
                    .invoice { max-width: 800px; margin: 0 auto; padding: 20px; }
                    .header { text-align: center; margin-bottom: 30px; }
                    .info-section { margin-bottom: 20px; }
                    table { width: 100%%; border-collapse: collapse; }
                    th, td { padding: 10px; text-align: left; border-bottom: 1px solid #ddd; }
                    .total { font-weight: bold; font-size: 18px; }
                </style>
            </head>
            <body>
                <div class="invoice">
                    <div class="header">
                        <h1>HÓA ĐƠN ĐIỆN TỬ</h1>
                        <p>Số: %s</p>
                        <p>Ngày: %s</p>
                    </div>
                    
                    <div class="info-section">
                        <h3>Thông tin khách hàng:</h3>
                        <p>Tên: %s</p>
                        <p>Email: %s</p>
                        <p>Số điện thoại: %s</p>
                    </div>
                    
                    <div class="info-section">
                        <h3>Thông tin phiên sạc:</h3>
                        <p>Trạm sạc: %s</p>
                        <p>Điểm sạc: %s</p>
                        <p>Thời gian bắt đầu: %s</p>
                        <p>Thời gian kết thúc: %s</p>
                        <p>Điện năng: %.2f kWh</p>
                        <p>Thời lượng: %d phút</p>
                    </div>
                    
                    <table>
                        <tr>
                            <th>Mô tả</th>
                            <th>Số tiền (VND)</th>
                        </tr>
                        <tr>
                            <td>Chi phí điện năng</td>
                            <td>%.0f</td>
                        </tr>
                        <tr>
                            <td>Chi phí thời gian</td>
                            <td>%.0f</td>
                        </tr>
                        <tr>
                            <td>Giảm giá</td>
                            <td>-%.0f</td>
                        </tr>
                        <tr>
                            <td>Tạm tính</td>
                            <td>%.0f</td>
                        </tr>
                        <tr>
                            <td>VAT (%.0f%%)</td>
                            <td>%.0f</td>
                        </tr>
                        <tr class="total">
                            <td>TỔNG CỘNG</td>
                            <td>%.0f</td>
                        </tr>
                    </table>
                    
                    <div class="info-section">
                        <p><i>%s</i></p>
                    </div>
                </div>
            </body>
            </html>
            """,
                invoice.getInvoiceNumber(),
                invoice.getIssueDate().format(formatter),
                invoice.getCustomerName(),
                invoice.getCustomerEmail(),
                invoice.getCustomerPhone(),
                invoice.getStationName(),
                invoice.getPointCode(),
                invoice.getChargingStartTime().format(formatter),
                invoice.getChargingEndTime().format(formatter),
                invoice.getEnergyDelivered(),
                invoice.getDuration(),
                invoice.getEnergyCost(),
                invoice.getTimeCost(),
                invoice.getDiscount(),
                invoice.getTotalAmount(),
                invoice.getVatRate(),
                invoice.getVatAmount(),
                invoice.getFinalAmount(),
                invoice.getNotes()
        );
    }
}