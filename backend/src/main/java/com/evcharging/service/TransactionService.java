package com.evcharging.service;

import com.evcharging.entity.Transaction;
import com.evcharging.enums.TransactionStatus;
import com.evcharging.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepo;

    public TransactionService(TransactionRepository transactionRepo) {
        this.transactionRepo = transactionRepo;
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

    // Xác nhận thanh toán
    @Transactional
    public Transaction confirmPayment(Long transactionId, boolean success) {
        Transaction tx = transactionRepo.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));

        if (success) {
            tx.setStatus(TransactionStatus.SUCCESS);
            tx.setPaidAt(LocalDateTime.now());
        } else {
            tx.setStatus(TransactionStatus.FAILED);
        }
        return transactionRepo.save(tx);
    }
}