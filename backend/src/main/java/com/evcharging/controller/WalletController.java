package com.evcharging.controller;

import com.evcharging.entity.Wallet;
import com.evcharging.entity.WalletTransaction;
import com.evcharging.service.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallets")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    /**
     * Nạp tiền vào ví
     */
    @PostMapping("/{accountId}/deposit")
    public ResponseEntity<?> deposit(@PathVariable Long accountId,
                                     @RequestParam double amount,
                                     @RequestParam(required = false) String description) {
        try {
            WalletTransaction tx = walletService.deposit(accountId, amount, description);
            return ResponseEntity.ok(tx);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PostMapping("/{accountId}/create")
    public ResponseEntity<?> createWallet(@PathVariable Long accountId) {
        try {
            Wallet wallet = walletService.createWallet(accountId);
            return ResponseEntity.ok(wallet);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }


}