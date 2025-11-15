package com.evcharging.controller;

import com.evcharging.dto.DtoMapper;
import com.evcharging.entity.Wallet;
import com.evcharging.entity.WalletTransaction;
import com.evcharging.service.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallets")
public class WalletController {

    private final WalletService walletService;
    private final DtoMapper dtoMapper;

    public WalletController(WalletService walletService, DtoMapper dtoMapper) {
        this.walletService = walletService;
        this.dtoMapper = dtoMapper;
    }

    @PostMapping("/{accountId}/create")
    public ResponseEntity<?> createWallet(@PathVariable Long accountId) {
        try {
            Wallet wallet = walletService.createWallet(accountId);
            return ResponseEntity.ok(dtoMapper.toWalletDTO(wallet));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PostMapping("/{accountId}/deposit")
    public ResponseEntity<?> deposit(@PathVariable Long accountId,
                                     @RequestParam double amount,
                                     @RequestParam(required = false) String description) {
        try {
            WalletTransaction tx = walletService.deposit(accountId, amount, description);
            return ResponseEntity.ok(dtoMapper.toWalletTransactionDTO(tx));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }


}