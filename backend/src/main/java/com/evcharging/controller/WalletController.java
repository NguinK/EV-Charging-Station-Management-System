package com.evcharging.controller;

import com.evcharging.dto.DtoMapper;
import com.evcharging.entity.Wallet;
import com.evcharging.entity.WalletTransaction;
import com.evcharging.exception.InsufficientBalanceException;
import com.evcharging.exception.WalletNotActiveException;
import com.evcharging.exception.WalletNotFoundException;
import com.evcharging.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;
    private final DtoMapper dtoMapper;

    @PostMapping("/{accountId}/create")
    public ResponseEntity<?> createWallet(@PathVariable Long accountId) {
        try {
            Wallet wallet = walletService.createWallet(accountId);
            return ResponseEntity.status(HttpStatus.CREATED).body(dtoMapper.toWalletDTO(wallet));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    //Get wallet by account ID
    @GetMapping("/{accountId}")
    public ResponseEntity<?> getWallet(@PathVariable Long accountId) {
        try {
            Wallet wallet = walletService.getWallet(accountId);
            return ResponseEntity.ok(dtoMapper.toWalletDTO(wallet));
        } catch (WalletNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    //Get or create wallet
    @GetMapping("/{accountId}/get-or-create")
    public ResponseEntity<?> getOrCreateWallet(@PathVariable Long accountId) {
        try {
            Wallet wallet = walletService.getOrCreateWallet(accountId);
            return ResponseEntity.ok(dtoMapper.toWalletDTO(wallet));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    //Deposit money into wallet
    @PostMapping("/{accountId}/deposit")
    public ResponseEntity<?> deposit(@PathVariable Long accountId,
                                     @RequestParam double amount,
                                     @RequestParam(required = false) String description) {
        try {
            WalletTransaction tx = walletService.deposit(accountId, amount, description);
            return ResponseEntity.ok(dtoMapper.toWalletTransactionDTO(tx));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (WalletNotActiveException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    //Withdraw money from wallet
    @PostMapping("/{accountId}/withdraw")
    public ResponseEntity<?> withdraw(@PathVariable Long accountId,
                                      @RequestParam double amount,
                                      @RequestParam(required = false) String description) {
        try {
            WalletTransaction tx = walletService.withdraw(accountId, amount, description);
            return ResponseEntity.ok(dtoMapper.toWalletTransactionDTO(tx));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (InsufficientBalanceException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (WalletNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (WalletNotActiveException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    //Get transaction history
    @GetMapping("/{accountId}/transactions")
    public ResponseEntity<?> getTransactionHistory(@PathVariable Long accountId) {
        try {
            List<WalletTransaction> transactions = walletService.getTransactionHistory(accountId);
            return ResponseEntity.ok(
                    transactions.stream()
                            .map(dtoMapper::toWalletTransactionDTO)
                            .collect(Collectors.toList())
            );
        } catch (WalletNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    //Lock wallet
    @PostMapping("/{accountId}/lock")
    public ResponseEntity<?> lockWallet(@PathVariable Long accountId,
                                        @RequestParam String reason) {
        try {
            Wallet wallet = walletService.lockWallet(accountId, reason);
            return ResponseEntity.ok(dtoMapper.toWalletDTO(wallet));
        } catch (WalletNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    //Unlock wallet
    @PostMapping("/{accountId}/unlock")
    public ResponseEntity<?> unlockWallet(@PathVariable Long accountId) {
        try {
            Wallet wallet = walletService.unlockWallet(accountId);
            return ResponseEntity.ok(dtoMapper.toWalletDTO(wallet));
        } catch (WalletNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    // Exception Handlers
    @ExceptionHandler(WalletNotFoundException.class)
    public ResponseEntity<String> handleWalletNotFound(WalletNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<String> handleInsufficientBalance(InsufficientBalanceException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(WalletNotActiveException.class)
    public ResponseEntity<String> handleWalletNotActive(WalletNotActiveException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }
}