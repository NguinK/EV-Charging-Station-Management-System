package com.evcharging.controller;

import com.evcharging.entity.Invoice;
import com.evcharging.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    /**
     * Lấy hóa đơn theo ID
     * GET /api/invoices/{invoiceId}
     */
    @GetMapping("/{invoiceId}")
    public ResponseEntity<Invoice> getInvoice(@PathVariable Long invoiceId) {
        Invoice invoice = invoiceService.getInvoice(invoiceId);
        return ResponseEntity.ok(invoice);
    }

    /**
     * Lấy hóa đơn theo số hóa đơn
     * GET /api/invoices/number/{invoiceNumber}
     */
    @GetMapping("/number/{invoiceNumber}")
    public ResponseEntity<Invoice> getInvoiceByNumber(@PathVariable String invoiceNumber) {
        Invoice invoice = invoiceService.getInvoiceByNumber(invoiceNumber);
        return ResponseEntity.ok(invoice);
    }

    /**
     * Lấy hóa đơn theo payment
     * GET /api/invoices/payment/{paymentId}
     */
    @GetMapping("/payment/{paymentId}")
    public ResponseEntity<Invoice> getInvoiceByPayment(@PathVariable Long paymentId) {
        Invoice invoice = invoiceService.getInvoiceByPayment(paymentId);
        return ResponseEntity.ok(invoice);
    }

    /**
     * Xuất hóa đơn dạng HTML
     * GET /api/invoices/{invoiceId}/html
     */
    @GetMapping("/{invoiceId}/html")
    public ResponseEntity<String> getInvoiceHTML(@PathVariable Long invoiceId) {
        String html = invoiceService.generateInvoiceHTML(invoiceId);
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(html);
    }

    /**
     * Xuất hóa đơn dạng PDF
     * GET /api/invoices/{invoiceId}/pdf
     */
    @GetMapping("/{invoiceId}/pdf")
    public ResponseEntity<byte[]> getInvoicePDF(@PathVariable Long invoiceId) {
        byte[] pdf = invoiceService.exportInvoicePDF(invoiceId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "invoice-" + invoiceId + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdf);
    }
}