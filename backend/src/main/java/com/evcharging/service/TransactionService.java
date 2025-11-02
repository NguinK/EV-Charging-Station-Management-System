package com.evcharging.service;

import com.evcharging.dto.DtoMapper;
import com.evcharging.dto.TransactionDTO;
import com.evcharging.entity.ChargingSession;
import com.evcharging.entity.Invoice;
import com.evcharging.entity.Transaction;
import com.evcharging.enums.PaymentMethod;
import com.evcharging.enums.TransactionStatus;
import com.evcharging.enums.TransactionType;
import com.evcharging.repository.InvoiceRepository;
import com.evcharging.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
@Slf4j
@Service
public class TransactionService {

    private final TransactionRepository transactionRepo;
    private final InvoiceRepository invoiceRepo;
    private final DtoMapper dtoMapper;
    private final WalletService walletService;
    private final InvoiceService invoiceService;

    public TransactionService(TransactionRepository transactionRepo
    , InvoiceRepository invoiceRepo,
                              DtoMapper dtoMapper,
                              WalletService walletService,
                              InvoiceService invoiceService) {
        this.transactionRepo = transactionRepo;
        this.invoiceRepo = invoiceRepo;
        this.dtoMapper = dtoMapper;
        this.walletService = walletService;
        this.invoiceService = invoiceService;
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


    public Transaction createTransaction(ChargingSession session,
                                         double finalCost,
                                         TransactionStatus status) {
        Transaction tx = new Transaction();
        tx.setSession(session);
        tx.setDriver(session.getDriver());
        tx.setAmount(finalCost);
        tx.setCurrency("VND");
        tx.setInvoiceNumber("INV-" + System.currentTimeMillis());
        tx.setTimestamp(LocalDateTime.now());
        tx.setStatus(status);
        tx.setType(TransactionType.PAYMENT);

        return tx;
    }

}