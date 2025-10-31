package com.evcharging.controller;

import com.evcharging.entity.Wallet;
import com.evcharging.entity.WalletTransaction;
import com.evcharging.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    /**
     * Tạo ví mới
     * POST /api/wallet/create
     */
    @PostMapping("/create")
    public ResponseEntity<Wallet> createWallet(
            @AuthenticationPrincipal Long accountId) {

        Wallet wallet = walletService.createWallet(accountId);
        return ResponseEntity.ok(wallet);
    }

    /**
     * Lấy thông tin ví
     * GET /api/wallet
     */
    @GetMapping
    public ResponseEntity<Wallet> getWallet(
            @AuthenticationPrincipal Long accountId) {

        Wallet wallet = walletService.getWallet(accountId);
        return ResponseEntity.ok(wallet);
    }

    /**
     * Nạp tiền vào ví
     * POST /api/wallet/deposit
     */
    @PostMapping("/deposit")
    public ResponseEntity<WalletTransaction> deposit(
            @AuthenticationPrincipal Long accountId,
            @RequestParam double amount,
            @RequestParam(required = false) String description) {

        WalletTransaction transaction = walletService.deposit(accountId, amount, description);
        return ResponseEntity.ok(transaction);
    }

    /**
     * Rút tiền từ ví
     * POST /api/wallet/withdraw
     */
    @PostMapping("/withdraw")
    public ResponseEntity<WalletTransaction> withdraw(
            @AuthenticationPrincipal Long accountId,
            @RequestParam double amount,
            @RequestParam(required = false) String description) {

        WalletTransaction transaction = walletService.withdraw(accountId, amount, description);
        return ResponseEntity.ok(transaction);
    }

    /**
     * Lấy lịch sử giao dịch
     * GET /api/wallet/transactions
     */
    @GetMapping("/transactions")
    public ResponseEntity<List<WalletTransaction>> getTransactions(
            @AuthenticationPrincipal Long accountId) {

        List<WalletTransaction> transactions = walletService.getTransactionHistory(accountId);
        return ResponseEntity.ok(transactions);
    }

    /**
     * Lấy lịch sử giao dịch theo khoảng thời gian
     * GET /api/wallet/transactions/range
     */
    @GetMapping("/transactions/range")
    public ResponseEntity<List<WalletTransaction>> getTransactionsByRange(
            @AuthenticationPrincipal Long accountId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        List<WalletTransaction> transactions = walletService.getTransactionHistory(accountId, startDate, endDate);
        return ResponseEntity.ok(transactions);
    }
}