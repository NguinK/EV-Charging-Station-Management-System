package com.evcharging.service;

import com.evcharging.entity.Account;
import com.evcharging.entity.Wallet;
import com.evcharging.entity.WalletTransaction;
import com.evcharging.enums.TransactionType;
import com.evcharging.enums.WalletStatus;
import com.evcharging.exception.InsufficientBalanceException;
import com.evcharging.exception.WalletNotActiveException;
import com.evcharging.exception.WalletNotFoundException;
import com.evcharging.repository.AccountRepository;
import com.evcharging.repository.WalletRepository;
import com.evcharging.repository.WalletTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepo;
    private final WalletTransactionRepository transactionRepo;
    private final AccountRepository accountRepo;

    //Tạo ví mới
    @Transactional
    public Wallet createWallet(Long accountId) {
        log.info("Creating wallet for account: {}", accountId);

        Account account = accountRepo.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found with ID: " + accountId));

        // Kiểm tra đã có ví chưa
        if (walletRepo.findByAccountId(accountId).isPresent()) {
            throw new RuntimeException("Wallet already exists for this account");
        }

        Wallet wallet = new Wallet();
        wallet.setAccount(account);
        wallet.setBalance(0.0);
        wallet.setStatus(WalletStatus.ACTIVE);

        wallet = walletRepo.save(wallet);

        log.info("Wallet created successfully: walletId={}, accountId={}", wallet.getId(), accountId);

        return wallet;
    }

    //Nạp tiền vào ví
    @Transactional
    public WalletTransaction deposit(Long accountId, double amount, String description) {
        log.info("Depositing {} VND to account: {}", amount, accountId);

        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }

        Wallet wallet = getOrCreateWallet(accountId);

        if (wallet.getStatus() != WalletStatus.ACTIVE) {
            throw new WalletNotActiveException(wallet.getStatus());
        }

        double balanceBefore = wallet.getBalance();
        double balanceAfter = balanceBefore + amount;

        // Cập nhật số dư
        wallet.setBalance(balanceAfter);
        walletRepo.save(wallet);

        // Tạo transaction record
        WalletTransaction transaction = new WalletTransaction();
        transaction.setWallet(wallet);
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setAmount(amount);
        transaction.setBalanceBefore(balanceBefore);
        transaction.setBalanceAfter(balanceAfter);
        transaction.setDescription(description != null ? description : "Deposit");

        transaction = transactionRepo.save(transaction);

        log.info("Deposit completed. New balance: {} VND", balanceAfter);

        return transaction;
    }

    //Rút tiền từ ví
    @Transactional
    public WalletTransaction withdraw(Long accountId, double amount, String description) {
        log.info("Withdrawing {} VND from account: {}", amount, accountId);

        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }

        Wallet wallet = getWallet(accountId);

        if (wallet.getStatus() != WalletStatus.ACTIVE) {
            throw new WalletNotActiveException(wallet.getStatus());
        }

        if (wallet.getBalance() < amount) {
            throw new InsufficientBalanceException(amount, wallet.getBalance());
        }

        double balanceBefore = wallet.getBalance();
        double balanceAfter = balanceBefore - amount;

        // Cập nhật số dư
        wallet.setBalance(balanceAfter);
        walletRepo.save(wallet);

        // Tạo transaction record
        WalletTransaction transaction = new WalletTransaction();
        transaction.setWallet(wallet);
        transaction.setType(TransactionType.WITHDRAWAL);
        transaction.setAmount(amount);
        transaction.setBalanceBefore(balanceBefore);
        transaction.setBalanceAfter(balanceAfter);
        transaction.setDescription(description != null ? description : "Withdrawal");

        transaction = transactionRepo.save(transaction);

        log.info("Withdrawal completed. New balance: {} VND", balanceAfter);

        return transaction;
    }

    //Trừ tiền (dùng cho thanh toán)
    @Transactional
    public WalletTransaction deductBalance(Long accountId, double amount, String description) {
        log.info("Deducting {} VND from account: {}", amount, accountId);

        if (amount <= 0) {
            throw new IllegalArgumentException("Deduction amount must be positive");
        }

        Wallet wallet = getWallet(accountId);

        if (wallet.getStatus() != WalletStatus.ACTIVE) {
            throw new WalletNotActiveException(wallet.getStatus());
        }

        if (wallet.getBalance() < amount) {
            throw new InsufficientBalanceException(amount, wallet.getBalance());
        }

        double balanceBefore = wallet.getBalance();
        double balanceAfter = balanceBefore - amount;

        wallet.setBalance(balanceAfter);
        walletRepo.save(wallet);

        WalletTransaction transaction = new WalletTransaction();
        transaction.setWallet(wallet);
        transaction.setType(TransactionType.PAYMENT);
        transaction.setAmount(amount);
        transaction.setBalanceBefore(balanceBefore);
        transaction.setBalanceAfter(balanceAfter);
        transaction.setDescription(description);

        transaction = transactionRepo.save(transaction);

        log.info("Deduction completed. New balance: {} VND", balanceAfter);

        return transaction;
    }

    //Cộng tiền (dùng cho hoàn tiền)
    @Transactional
    public WalletTransaction addBalance(Long accountId, double amount, String description) {
        log.info("Adding {} VND to account: {}", amount, accountId);

        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        Wallet wallet = getOrCreateWallet(accountId);

        double balanceBefore = wallet.getBalance();
        double balanceAfter = balanceBefore + amount;

        wallet.setBalance(balanceAfter);
        walletRepo.save(wallet);

        WalletTransaction transaction = new WalletTransaction();
        transaction.setWallet(wallet);
        transaction.setType(TransactionType.REFUND);
        transaction.setAmount(amount);
        transaction.setBalanceBefore(balanceBefore);
        transaction.setBalanceAfter(balanceAfter);
        transaction.setDescription(description);

        transaction = transactionRepo.save(transaction);

        log.info("Balance added. New balance: {} VND", balanceAfter);

        return transaction;
    }

    //Lấy thông tin ví
    public Wallet getWallet(Long accountId) {
        return walletRepo.findByAccountId(accountId)
                .orElseThrow(() -> new WalletNotFoundException(accountId));
    }

    //Lấy lịch sử giao dịch
    public List<WalletTransaction> getTransactionHistory(Long accountId) {
        Wallet wallet = getWallet(accountId);
        return transactionRepo.findByWalletIdOrderByCreatedAtDesc(wallet.getId());
    }

    //Lấy lịch sử giao dịch theo khoảng thời gian
    public List<WalletTransaction> getTransactionHistory(Long accountId,
                                                         OffsetDateTime startDate,
                                                         OffsetDateTime endDate) {
        Wallet wallet = getWallet(accountId);
        return transactionRepo.findByWalletIdAndDateRange(wallet.getId(), startDate, endDate);
    }

    //Khóa ví
    @Transactional
    public Wallet lockWallet(Long accountId, String reason) {
        log.warn("Locking wallet for account: {}, Reason: {}", accountId, reason);

        Wallet wallet = getWallet(accountId);
        wallet.setStatus(WalletStatus.LOCKED);

        return walletRepo.save(wallet);
    }

    //Mở khóa ví
    @Transactional
    public Wallet unlockWallet(Long accountId) {
        log.info("Unlocking wallet for account: {}", accountId);

        Wallet wallet = getWallet(accountId);
        wallet.setStatus(WalletStatus.ACTIVE);

        return walletRepo.save(wallet);
    }

    //Lấy hoặc tạo ví mới nếu chưa có
    public Wallet getOrCreateWallet(Long accountId) {
        return walletRepo.findByAccountId(accountId)
                .orElseGet(() -> createWallet(accountId));
    }
}