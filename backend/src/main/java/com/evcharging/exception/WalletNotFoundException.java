package com.evcharging.exception;

public class WalletNotFoundException extends RuntimeException {
    public WalletNotFoundException(String message) {
        super(message);
    }

    public WalletNotFoundException(Long accountId) {
        super("Wallet not found for account ID: " + accountId);
    }
}