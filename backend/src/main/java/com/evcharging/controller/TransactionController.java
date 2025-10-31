package com.evcharging.controller;

import com.evcharging.dto.TransactionDTO;
import com.evcharging.entity.Transaction;
import com.evcharging.enums.PaymentMethod;
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

    @PostMapping("/transactions/{id}/pay")
    public ResponseEntity<TransactionDTO> pay(
            @PathVariable Long id,
            @RequestParam PaymentMethod method,
            @RequestParam boolean success) {
        TransactionDTO dto = transactionService.updateTransaction(id, method, success);
        return ResponseEntity.ok(dto);
    }
}