package com.evcharging.service;

import com.evcharging.dto.DtoMapper;
import com.evcharging.dto.InvoiceDTO;
import com.evcharging.entity.Invoice;
import com.evcharging.entity.Transaction;
import com.evcharging.enums.PaymentMethod;
import com.evcharging.enums.TransactionStatus;
import com.evcharging.repository.TransactionRepository;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.time.OffsetDateTime;

@Service
public class PaymentService {

    private final TransactionRepository transactionRepo;
    private final WalletService walletService;
    private final InvoiceService invoiceService;
    private final EmailService emailService;
    private final DtoMapper dtoMapper;
    private final InvoicePdfGenerator invoicePdfGenerator;

    public PaymentService(InvoicePdfGenerator invoicePdfGenerator,
                          TransactionRepository transactionRepo,
                          WalletService walletService,
                          InvoiceService invoiceService,
                          EmailService emailService, DtoMapper dtoMapper) {
        this.invoicePdfGenerator = invoicePdfGenerator;
        this.transactionRepo = transactionRepo;
        this.walletService = walletService;
        this.invoiceService = invoiceService;
        this.emailService = emailService;
        this.dtoMapper = dtoMapper;
    }

    @Transactional
    public Transaction payWithEWallet(Long transactionId) throws MessagingException, IOException {
        Transaction tx = transactionRepo.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (tx.getStatus() != TransactionStatus.PENDING) {
            throw new IllegalStateException("Transaction is not pending");
        }

        tx.setPaymentMethod(PaymentMethod.EWALLET);
        tx.setPaidAt(OffsetDateTime.now());

        // Nếu trừ tiền fail -> rollback, transaction vẫn PENDING
        walletService.deductBalance(
                tx.getDriver().getAccount().getId(),
                tx.getAmount().doubleValue(),
                "Thanh toán phiên sạc #" + tx.getSession().getId()
        );

        // Thành công thì update SUCCESS + tạo invoice
        tx.setStatus(TransactionStatus.SUCCESS);
        Invoice invoice = invoiceService.createInvoice(tx);
        InvoiceDTO dto = dtoMapper.toDTO(invoice);
        byte[] pdf = invoicePdfGenerator.generate(dto);

        emailService.sendInvoice(
                invoice.getCustomerEmail(),   // hoặc dto.getCustomerEmail()
                "Hóa đơn điện tử #" + dto.getInvoiceNumber(),
                "Xin chào " + dto.getCustomerName() + ",\n\nĐính kèm là hóa đơn điện tử cho phiên sạc tại " + dto.getStationName(),
                pdf
        );

        return transactionRepo.save(tx);
    }
}

//    @Transactional
//    public Transaction payWithBanking(Long transactionId) {
//        Transaction tx = transactionRepo.findById(transactionId)
//                .orElseThrow(() -> new RuntimeException("Transaction not found"));
//
//        if (tx.getStatus() != TransactionStatus.PENDING) {
//            throw new IllegalStateException("Transaction is not pending");
//        }
//
//        tx.setPaymentMethod(PaymentMethod.BANKING);
//        tx.setStatus(TransactionStatus.PENDING);
//
//        // Tạo link/QR từ cổng thanh toán
//        String returnUrl = "https://your-frontend.com/payment/result";
//        String paymentUrl = paymentGatewayService.redirectToGateway(tx, returnUrl);
//        tx.setPaymentUrl(paymentUrl);
//
//        return transactionRepo.save(tx);
//    }
