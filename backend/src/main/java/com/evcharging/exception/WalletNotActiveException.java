package com.evcharging.exception;

import com.evcharging.enums.WalletStatus;

public class WalletNotActiveException extends RuntimeException {
    public WalletNotActiveException(String message) {
        super(message);
    }

    public WalletNotActiveException(WalletStatus status) {
        super("Wallet is not active. Current status: " + status);
    }
}