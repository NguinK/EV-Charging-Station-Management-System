package com.evcharging.controller;

import com.evcharging.entity.Transaction;
import com.evcharging.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
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

    // Xác nhận thanh toán
    @PostMapping("/{transactionId}/confirmPayment")
    public ResponseEntity<Transaction> confirmPayment(
            @PathVariable Long transactionId,
            @RequestParam boolean success) {
        return ResponseEntity.ok(transactionService.confirmPayment(transactionId, success));
    }
}