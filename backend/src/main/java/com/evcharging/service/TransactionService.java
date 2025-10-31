package com.evcharging.service;

import com.evcharging.dto.DtoMapper;
import com.evcharging.dto.TransactionDTO;
import com.evcharging.entity.Invoice;
import com.evcharging.entity.Transaction;
import com.evcharging.enums.PaymentMethod;
import com.evcharging.enums.TransactionStatus;
import com.evcharging.repository.InvoiceRepository;
import com.evcharging.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepo;
    private final InvoiceRepository invoiceRepo;
    private final DtoMapper dtoMapper;

    public TransactionService(TransactionRepository transactionRepo
    , InvoiceRepository invoiceRepo,
                              DtoMapper dtoMapper) {
        this.transactionRepo = transactionRepo;
        this.invoiceRepo = invoiceRepo;
        this.dtoMapper = dtoMapper;
    }

    // Lấy lịch sử giao dịch của 1 driver
    public List<Transaction> getTransactionsByDriver(Long driverId) {
        return transactionRepo.findAllByDriverId(driverId);
    }

    // Lấy transaction theo session
    public Transaction getTransactionBySession(Long sessionId) {
        return transactionRepo.findBySessionId(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found for session"));
    }

    @Transactional
    public TransactionDTO updateTransaction(Long transactionId, PaymentMethod method, boolean success) {
        Transaction tx = transactionRepo.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (tx.getStatus() != TransactionStatus.PENDING) {
            throw new IllegalStateException("Transaction is not pending");
        }

        tx.setPaymentMethod(method);
        tx.setPaidAt(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
        tx.setStatus(success ? TransactionStatus.SUCCESS : TransactionStatus.FAILED);

        Transaction saved = transactionRepo.save(tx);

        // Nếu thành công thì sinh invoice
        if (success) {
            Invoice invoice = new Invoice();
            invoice.setTransaction(saved);
            invoice.setInvoiceNumber(saved.getInvoiceNumber());
            invoice.setCreatedAt(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
            invoiceRepo.save(invoice);
        }

        return DtoMapper.toTransactionDTO(saved);
    }

}