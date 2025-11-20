package com.evcharging.exception;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(String message) {
        super(message);
    }

    public InsufficientBalanceException(double required, double available) {
        super(String.format("Insufficient balance. Required: %.2f VND, Available: %.2f VND", required, available));
    }
}