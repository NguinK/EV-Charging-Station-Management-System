package com.evcharging.service;

import com.evcharging.dto.InvoiceDTO;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
@Component
@Slf4j
public class InvoicePdfGenerator {
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final NumberFormat CURRENCY_FORMAT =
            NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN"));
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,##0.00");

    // Brand colors
    private static final DeviceRgb PRIMARY_COLOR = new DeviceRgb(0, 123, 255);
    private static final DeviceRgb SECONDARY_COLOR = new DeviceRgb(108, 117, 125);
    private static final DeviceRgb SUCCESS_COLOR = new DeviceRgb(40, 167, 69);
    private static final DeviceRgb WARNING_COLOR = new DeviceRgb(255, 193, 7);

    //Generate PDF invoice with full details
    public byte[] generate(InvoiceDTO invoice) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdf = new PdfDocument(writer);
        Document doc = new Document(pdf);

        try {
            // Set font (optional - for Vietnamese support)
             PdfFont font = PdfFontFactory.createFont("fonts/NotoSerif-VariableFont_wdth,wght.ttf", PdfEncodings.IDENTITY_H);
             doc.setFont(font);

            // 1. Header - Company Info
            addCompanyHeader(doc);

            // 2. Invoice Title
            addInvoiceTitle(doc, invoice);

            // 3. Customer & Transaction Info
            addCustomerInfo(doc, invoice);

            // 4. Charging Session Details
            addChargingDetails(doc, invoice);

            // 5. Price Breakdown Table
            addPriceBreakdown(doc, invoice);

            // 6. Total Amount
            addTotalAmount(doc, invoice);

            // 7. Payment Info
            addPaymentInfo(doc, invoice);

            // 8. Footer - Terms & Signature
            addFooter(doc, invoice);

            log.info("PDF invoice generated successfully for invoice: {}", invoice.getInvoiceNumber());

        } catch (Exception e) {
            log.error("Error generating PDF invoice", e);
            throw new IOException("Failed to generate PDF invoice", e);
        } finally {
            doc.close();
        }

        return out.toByteArray();
    }

    //1. Company Header
    private void addCompanyHeader(Document doc) {
        // Company name
        Paragraph companyName = new Paragraph("CÔNG TY TNHH TRẠM SẠC ĐIỆN XE")
                .setBold()
                .setFontSize(16)
                .setFontColor(PRIMARY_COLOR)
                .setTextAlignment(TextAlignment.CENTER);
        doc.add(companyName);

        // Company details
        Paragraph companyDetails = new Paragraph(
                "Địa chỉ: 123 Đường ABC, Quận 1, TP. Hồ Chí Minh\n" +
                        "Hotline: 1900-xxxx | Email: support@evcharging.vn\n" +
                        "MST: 0123456789"
        )
                .setFontSize(9)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(SECONDARY_COLOR);
        doc.add(companyDetails);

        // Separator line
        doc.add(new Paragraph("\n"));
    }

    //2. Invoice Title
    private void addInvoiceTitle(Document doc, InvoiceDTO invoice) {
        Paragraph title = new Paragraph("HÓA ĐƠN ĐIỆN TỬ")
                .setBold()
                .setFontSize(20)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(PRIMARY_COLOR);
        doc.add(title);

        Paragraph subtitle = new Paragraph("DỊCH VỤ SẠC ĐIỆN XE")
                .setFontSize(12)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(SECONDARY_COLOR);
        doc.add(subtitle);

        doc.add(new Paragraph("\n"));
    }

    //3. Customer & Transaction Info
    private void addCustomerInfo(Document doc, InvoiceDTO invoice) {
        Table infoTable = new Table(new float[]{1, 1});
        infoTable.setWidth(UnitValue.createPercentValue(100));

        // Left column - Invoice info
        Table leftTable = new Table(1);
        leftTable.addCell(createInfoCell("Số hóa đơn:", invoice.getInvoiceNumber(), true));
        leftTable.addCell(createInfoCell("Ngày xuất:",
                invoice.getIssuedAt() != null ? invoice.getIssuedAt().format(DATE_TIME_FORMATTER) : "N/A", false));
        leftTable.addCell(createInfoCell("Trạng thái:",
                getStatusDisplay(invoice.getStatus()), false));

        // Right column - Customer info
        Table rightTable = new Table(1);
        rightTable.addCell(createInfoCell("Khách hàng:", invoice.getCustomerName(), true));
        rightTable.addCell(createInfoCell("Số điện thoại:",
                invoice.getCustomerPhone() != null ? invoice.getCustomerPhone() : "N/A", false));
        rightTable.addCell(createInfoCell("Email:",
                invoice.getCustomerEmail() != null ? invoice.getCustomerEmail() : "N/A", false));

        infoTable.addCell(new Cell().add(leftTable).setBorder(Border.NO_BORDER));
        infoTable.addCell(new Cell().add(rightTable).setBorder(Border.NO_BORDER));

        doc.add(infoTable);
        doc.add(new Paragraph("\n"));
    }

    //4. Charging Session Details
    private void addChargingDetails(Document doc, InvoiceDTO invoice) {
        // Section title
        Paragraph sectionTitle = new Paragraph("THÔNG TIN PHIÊN SẠC")
                .setBold()
                .setFontSize(12)
                .setFontColor(PRIMARY_COLOR)
                .setUnderline();
        doc.add(sectionTitle);
        doc.add(new Paragraph("\n"));

        Table detailsTable = new Table(new float[]{1, 2});
        detailsTable.setWidth(UnitValue.createPercentValue(100));

        detailsTable.addCell(createTableCell("Trạm sạc:", true));
        detailsTable.addCell(createTableCell(invoice.getStationName(), false));

        detailsTable.addCell(createTableCell("Điểm sạc:", true));
        detailsTable.addCell(createTableCell(invoice.getPointCode(), false));

        if (invoice.getStartTime() != null) {
            detailsTable.addCell(createTableCell("Thời gian bắt đầu:", true));
            detailsTable.addCell(createTableCell(invoice.getStartTime().format(DATE_TIME_FORMATTER), false));
        }

        if (invoice.getEndTime() != null) {
            detailsTable.addCell(createTableCell("Thời gian kết thúc:", true));
            detailsTable.addCell(createTableCell(invoice.getEndTime().format(DATE_TIME_FORMATTER), false));
        }

        if (invoice.getEnergyConsumed() != null) {
            detailsTable.addCell(createTableCell("Năng lượng tiêu thụ:", true));
            detailsTable.addCell(createTableCell(DECIMAL_FORMAT.format(invoice.getEnergyConsumed()) + " kWh", false));
        }

        if (invoice.getDurationMinutes() != null) {
            detailsTable.addCell(createTableCell("Thời gian sạc:", true));
            detailsTable.addCell(createTableCell(formatDuration(invoice.getDurationMinutes()), false));
        }

        doc.add(detailsTable);
        doc.add(new Paragraph("\n"));
    }

    //5. Price Breakdown Table
    private void addPriceBreakdown(Document doc, InvoiceDTO invoice) {
        // Section title
        Paragraph sectionTitle = new Paragraph("CHI TIẾT GIÁ")
                .setBold()
                .setFontSize(12)
                .setFontColor(PRIMARY_COLOR)
                .setUnderline();
        doc.add(sectionTitle);
        doc.add(new Paragraph("\n"));

        Table priceTable = new Table(new float[]{3, 1, 1, 2});
        priceTable.setWidth(UnitValue.createPercentValue(100));

        // Header
        priceTable.addHeaderCell(createHeaderCell("Hạng mục"));
        priceTable.addHeaderCell(createHeaderCell("Số lượng"));
        priceTable.addHeaderCell(createHeaderCell("Đơn giá"));
        priceTable.addHeaderCell(createHeaderCell("Thành tiền"));

        // Energy fee
        if (invoice.getEnergyFee() != null && invoice.getEnergyFee() > 0) {
            priceTable.addCell(createTableCell("Phí năng lượng", false));
            priceTable.addCell(createTableCell(
                    invoice.getEnergyConsumed() != null ? DECIMAL_FORMAT.format(invoice.getEnergyConsumed()) + " kWh" : "-",
                    false
            ));
            priceTable.addCell(createTableCell(
                    invoice.getPricePerKwh() != null ? formatCurrency(invoice.getPricePerKwh()) : "-",
                    false
            ));
            priceTable.addCell(createTableCell(formatCurrency(invoice.getEnergyFee()), false));
        }

        // Time fee
        if (invoice.getTimeFee() != null && invoice.getTimeFee() > 0) {
            priceTable.addCell(createTableCell("Phí thời gian sạc", false));
            priceTable.addCell(createTableCell(
                    invoice.getDurationMinutes() != null ? invoice.getDurationMinutes() + " phút" : "-",
                    false
            ));
            priceTable.addCell(createTableCell(
                    invoice.getPricePerMinute() != null ? formatCurrency(invoice.getPricePerMinute()) : "-",
                    false
            ));
            priceTable.addCell(createTableCell(formatCurrency(invoice.getTimeFee()), false));
        }

        // Service fee
        if (invoice.getServiceFee() != null && invoice.getServiceFee() > 0) {
            priceTable.addCell(createTableCell("Phí dịch vụ", false));
            priceTable.addCell(createTableCell("-", false));
            priceTable.addCell(createTableCell("-", false));
            priceTable.addCell(createTableCell(formatCurrency(invoice.getServiceFee()), false));
        }

        // Reservation fee
        if (invoice.getReservationFee() != null && invoice.getReservationFee() > 0) {
            priceTable.addCell(createTableCell("Phí đặt chỗ", false));
            priceTable.addCell(createTableCell("-", false));
            priceTable.addCell(createTableCell("-", false));
            priceTable.addCell(createTableCell(formatCurrency(invoice.getReservationFee()), false));
        }

        doc.add(priceTable);
        doc.add(new Paragraph("\n"));
    }

    /**
     * 6. Total Amount
     */
    private void addTotalAmount(Document doc, InvoiceDTO invoice) {
        Table totalTable = new Table(new float[]{3, 2});
        totalTable.setWidth(UnitValue.createPercentValue(100));

        // Subtotal
        if (invoice.getSubtotal() != null) {
            totalTable.addCell(createTableCell("Tạm tính:", true).setTextAlignment(TextAlignment.RIGHT));
            totalTable.addCell(createTableCell(formatCurrency(invoice.getSubtotal()), false)
                    .setTextAlignment(TextAlignment.RIGHT));
        }

        // Discount
        if (invoice.getDiscount() != null && invoice.getDiscount() > 0) {
            totalTable.addCell(createTableCell("Giảm giá:", true)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setFontColor(SUCCESS_COLOR));
            totalTable.addCell(createTableCell("- " + formatCurrency(invoice.getDiscount()), false)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setFontColor(SUCCESS_COLOR));
        }

        // Tax/VAT
        if (invoice.getTax() != null && invoice.getTax() > 0) {
            totalTable.addCell(createTableCell("Thuế VAT (10%):", true).setTextAlignment(TextAlignment.RIGHT));
            totalTable.addCell(createTableCell(formatCurrency(invoice.getTax()), false)
                    .setTextAlignment(TextAlignment.RIGHT));
        }

        // Final total
        Cell totalLabelCell = new Cell()
                .add(new Paragraph("TỔNG CỘNG:")
                        .setBold()
                        .setFontSize(14)
                        .setFontColor(PRIMARY_COLOR))
                .setTextAlignment(TextAlignment.RIGHT)
                .setBorder(Border.NO_BORDER)
                .setBorderTop(new SolidBorder(ColorConstants.BLACK, 1))
                .setPaddingTop(10);

        Cell totalAmountCell = new Cell()
                .add(new Paragraph(formatCurrency(invoice.getFinalAmount()))
                        .setBold()
                        .setFontSize(14)
                        .setFontColor(PRIMARY_COLOR))
                .setTextAlignment(TextAlignment.RIGHT)
                .setBorder(Border.NO_BORDER)
                .setBorderTop(new SolidBorder(ColorConstants.BLACK, 1))
                .setPaddingTop(10);

        totalTable.addCell(totalLabelCell);
        totalTable.addCell(totalAmountCell);

        doc.add(totalTable);
        doc.add(new Paragraph("\n"));
    }

    //7. Payment Info
    private void addPaymentInfo(Document doc, InvoiceDTO invoice) {
        Paragraph sectionTitle = new Paragraph("THÔNG TIN THANH TOÁN")
                .setBold()
                .setFontSize(12)
                .setFontColor(PRIMARY_COLOR)
                .setUnderline();
        doc.add(sectionTitle);
        doc.add(new Paragraph("\n"));

        Table paymentTable = new Table(new float[]{1, 2});
        paymentTable.setWidth(UnitValue.createPercentValue(100));

        if (invoice.getPaymentMethod() != null) {
            paymentTable.addCell(createTableCell("Phương thức:", true));
            paymentTable.addCell(createTableCell(getPaymentMethodDisplay(invoice.getPaymentMethod()), false));
        }

        if (invoice.getPaidAt() != null) {
            paymentTable.addCell(createTableCell("Thời gian thanh toán:", true));
            paymentTable.addCell(createTableCell(invoice.getPaidAt().format(DATE_TIME_FORMATTER), false));
        }

        if (invoice.getTransactionId() != null) {
            paymentTable.addCell(createTableCell("Mã giao dịch:", true));
            paymentTable.addCell(createTableCell(invoice.getTransactionId().toString(), false));
        }

        if (invoice.getProcessedByStaffName() != null) {
            paymentTable.addCell(createTableCell("Nhân viên xử lý:", true));
            paymentTable.addCell(createTableCell(invoice.getProcessedByStaffName(), false));
        }

        doc.add(paymentTable);
        doc.add(new Paragraph("\n"));
    }

    //8. Footer
    private void addFooter(Document doc, InvoiceDTO invoice) {
        // Terms & Conditions
        Paragraph terms = new Paragraph(
                "* Hóa đơn này được tạo tự động bởi hệ thống.\n" +
                        "* Vui lòng kiểm tra thông tin và liên hệ hotline nếu có thắc mắc.\n" +
                        "* Cảm ơn quý khách đã sử dụng dịch vụ!"
        )
                .setFontSize(8)
                .setFontColor(SECONDARY_COLOR)
                .setItalic();
        doc.add(terms);

        doc.add(new Paragraph("\n"));

        // Signature section
        Table signatureTable = new Table(new float[]{1, 1});
        signatureTable.setWidth(UnitValue.createPercentValue(100));

        Cell customerSignCell = new Cell()
                .add(new Paragraph("Khách hàng\n\n\n\n(Ký tên)")
                        .setTextAlignment(TextAlignment.CENTER))
                .setBorder(Border.NO_BORDER);

        Cell staffSignCell = new Cell()
                .add(new Paragraph("Nhân viên\n\n\n\n(Ký tên)")
                        .setTextAlignment(TextAlignment.CENTER))
                .setBorder(Border.NO_BORDER);

        signatureTable.addCell(customerSignCell);
        signatureTable.addCell(staffSignCell);

        doc.add(signatureTable);

        // Footer note
        Paragraph footer = new Paragraph(
                "\n_______________________________________________\n" +
                        "Hóa đơn được in lúc: " + java.time.LocalDateTime.now().format(DATE_TIME_FORMATTER)
        )
                .setFontSize(8)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(SECONDARY_COLOR);
        doc.add(footer);
    }

    //Helper Methods
    private Cell createInfoCell(String label, String value, boolean isBold) {
        Paragraph p = new Paragraph();
        p.add(new Paragraph(label).setBold().setFontSize(9));

        Paragraph valuePara = new Paragraph(value).setFontSize(9);
        if (isBold) {
            valuePara.setBold();
        }
        p.add(valuePara);

        return new Cell().add(p).setBorder(Border.NO_BORDER).setPadding(2);
    }

    private Cell createTableCell(String text, boolean isBold) {
        Paragraph p = new Paragraph(text).setFontSize(10);
        if (isBold) {
            p.setBold();
        }
        return new Cell().add(p).setPadding(5);
    }

    private Cell createHeaderCell(String text) {
        return new Cell()
                .add(new Paragraph(text).setBold().setFontSize(10).setFontColor(ColorConstants.WHITE))
                .setBackgroundColor(PRIMARY_COLOR)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(8);
    }

    private String formatCurrency(Double amount) {
        if (amount == null) return "0 đ";
        return String.format("%,.0f đ", amount);
    }

    private String formatDuration(Long minutes) {
        if (minutes == null) return "0 phút";
        long hours = minutes / 60;
        long mins = minutes % 60;
        if (hours > 0) {
            return String.format("%d giờ %d phút", hours, mins);
        }
        return mins + " phút";
    }

    private String getStatusDisplay(String status) {
        if (status == null) return "N/A";
        switch (status.toUpperCase()) {
            case "PAID": return "✓ Đã thanh toán";
            case "PENDING": return "○ Chờ thanh toán";
            case "CANCELLED": return "✗ Đã hủy";
            default: return status;
        }
    }

    private String getPaymentMethodDisplay(String method) {
        if (method == null) return "N/A";
        switch (method.toUpperCase()) {
            case "CASH": return "Tiền mặt";
            case "CARD": return "Thẻ tín dụng/ghi nợ";
            case "EWALLET": return "Ví điện tử";
            case "BANK_TRANSFER": return "Chuyển khoản ngân hàng";
            case "QR_CODE": return "QR Code";
            default: return method;
        }
    }
}