package com.evcharging.controller;

import com.evcharging.dto.DtoMapper;
import com.evcharging.dto.TransactionDTO;
import com.evcharging.entity.EVDriver;
import com.evcharging.entity.Transaction;
import com.evcharging.enums.PaymentMethod;
import com.evcharging.service.PaymentService;
import com.evcharging.repository.EVDriverRepository;
import com.evcharging.service.TransactionService;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final PaymentService paymentService;
    private final DtoMapper dtoMapper;
    private final EVDriverRepository evDriverRepository;

    public TransactionController(TransactionService transactionService, PaymentService paymentService, DtoMapper dtoMapper,  EVDriverRepository evDriverRepository) {
        this.transactionService = transactionService;
        this.paymentService = paymentService;
        this.dtoMapper = dtoMapper;
        this.evDriverRepository = evDriverRepository;
    }

    // Lấy lịch sử giao dịch của driver
    @GetMapping("/my-history")
    public ResponseEntity<List<TransactionDTO>> getMyTransactions(Authentication authentication) {
        String email = authentication.getName(); // lấy từ JWT
        EVDriver driver = evDriverRepository.findByAccountEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Driver not found"));

        List<Transaction> transactions = transactionService.getTransactionsByDriver(driver.getId());
        List<TransactionDTO> dtos = transactions.stream()
                .map(dtoMapper::toTransactionDTO)
                .toList();

        return ResponseEntity.ok(dtos);
    }

    // Lấy transaction theo session
    @GetMapping("/getTransactionfromSession/{sessionId}")
    public ResponseEntity<TransactionDTO> getTransactionBySession(@PathVariable Long sessionId) {
        Transaction tx = transactionService.getTransactionBySession(sessionId);
        return ResponseEntity.ok( dtoMapper.toTransactionDTO(tx));
    }

    @PostMapping("/{id}/pay-EWallet")
    public TransactionDTO payWithEWallet(@PathVariable("id") Long transactionId) throws MessagingException, IOException {
        Transaction tx = paymentService.payWithEWallet(transactionId);
        return dtoMapper.toTransactionDTO(tx);
    }

}