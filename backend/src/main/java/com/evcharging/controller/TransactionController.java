package com.evcharging.controller;

import com.evcharging.dto.DtoMapper;
import com.evcharging.dto.TransactionDTO;
import com.evcharging.entity.Transaction;
import com.evcharging.enums.PaymentMethod;
import com.evcharging.service.PaymentService;
import com.evcharging.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final PaymentService paymentService;

    public TransactionController(TransactionService transactionService, PaymentService paymentService) {
        this.transactionService = transactionService;
        this.paymentService = paymentService;
    }

    // Lấy lịch sử giao dịch của driver
    @GetMapping("/getDriverTransactionHistory/{driverId}")
    public ResponseEntity<List<Transaction>> getTransactionsByDriver(@PathVariable Long driverId) {
        return ResponseEntity.ok(transactionService.getTransactionsByDriver(driverId));
    }

    // Lấy transaction theo session
    @GetMapping("/getTransactionfromSession/{sessionId}")
    public ResponseEntity<Transaction> getTransactionBySession(@PathVariable Long sessionId) {
        return ResponseEntity.ok(transactionService.getTransactionBySession(sessionId));
    }

    @PostMapping("/{id}/pay-EWallet")
    public TransactionDTO payWithEWallet(@PathVariable("id") Long transactionId) {
        Transaction tx = paymentService.payWithEWallet(transactionId);
        return DtoMapper.toTransactionDTO(tx);
    }

}