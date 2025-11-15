package com.evcharging.service.staff.impl;

import com.evcharging.dto.staff.RecordPaymentRequest;
import com.evcharging.dto.staff.TransactionDetailResponse;
import com.evcharging.entity.Transaction;
import com.evcharging.entity.ChargingSession;
import com.evcharging.enums.PaymentMethod;
import com.evcharging.enums.TransactionStatus;
import com.evcharging.enums.TransactionType;
import com.evcharging.exception.BusinessException;
import com.evcharging.exception.ResourceNotFoundException;
import com.evcharging.repository.TransactionRepository;
import com.evcharging.repository.ChargingSessionRepository;
import com.evcharging.service.staff.StaffPaymentService;
import com.evcharging.service.staff.StaffStationService;
import com.evcharging.utils.SecurityUtils;
import com.evcharging.utils.InvoiceNumberGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StaffPaymentServiceImpl implements StaffPaymentService {

    private final TransactionRepository transactionRepository;
    private final ChargingSessionRepository chargingSessionRepository;
    private final StaffStationService staffStationService;
    private final SecurityUtils securityUtil;
    private final InvoiceNumberGenerator invoiceNumberGenerator;
    private final SecurityUtils securityUtils;

    @Override
    @Transactional
    public TransactionDetailResponse recordPayment(Long transactionId, RecordPaymentRequest request) {
        // Lấy transaction
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

        // Verify staff có quyền truy cập
        verifyStaffAccess(transaction);

        // Validate transaction status
        if (transaction.getStatus() != TransactionStatus.PENDING) {
            throw new BusinessException("Only pending transactions can be processed. Current status: "
                    + transaction.getStatus());
        }

        // Validate payment method
        PaymentMethod paymentMethod;
        try {
            paymentMethod = PaymentMethod.valueOf(request.getPaymentMethod().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Invalid payment method: " + request.getPaymentMethod());
        }

        // Cập nhật transaction
        transaction.setPaymentMethod(paymentMethod);
        transaction.setPaymentNotes(request.getPaymentNotes());
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setPaidAt(OffsetDateTime.now());
        transaction.setProcessedByStaffId(securityUtils.getCurrentStaffAccountId());

        // Generate invoice number nếu chưa có
        if (transaction.getInvoiceNumber() == null) {
            transaction.setInvoiceNumber(invoiceNumberGenerator.generate());
        }

        transaction = transactionRepository.save(transaction);

        log.info("Payment recorded: Transaction {} paid via {} by staff {}",
                transactionId, paymentMethod, securityUtils.getCurrentStaffAccountId());

        return mapToResponse(transaction);
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionDetailResponse getTransactionDetail(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

        verifyStaffAccess(transaction);

        return mapToResponse(transaction);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionDetailResponse> getPendingTransactions(Long stationId) {
        // Verify staff có quyền truy cập station
        if (!staffStationService.hasAccessToStation(stationId)) {
            throw new BusinessException("You do not have access to this station");
        }

        // Lấy tất cả charging sessions của station
        List<ChargingSession> sessions = chargingSessionRepository.findByStationId(stationId);

        // Lấy transactions pending
        List<Long> sessionIds = sessions.stream()
                .map(ChargingSession::getId)
                .collect(Collectors.toList());

        List<Transaction> pendingTransactions = transactionRepository
                .findByChargingSessionIdInAndStatus(sessionIds, TransactionStatus.PENDING);

        return pendingTransactions.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TransactionDetailResponse refundTransaction(Long transactionId, String reason) {
        Transaction originalTransaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

        verifyStaffAccess(originalTransaction);

        // Chỉ có thể refund transaction đã SUCCESS
        if (originalTransaction.getStatus() != TransactionStatus.SUCCESS) {
            throw new BusinessException("Only successful transactions can be refunded");
        }

        // Tạo transaction refund mới
        Transaction refund = new Transaction();
        refund.setAmount(originalTransaction.getAmount().negate()); // Số âm
        refund.setCurrency(originalTransaction.getCurrency());
        refund.setType(TransactionType.REFUND);
        refund.setStatus(TransactionStatus.SUCCESS);
        refund.setPaymentMethod(originalTransaction.getPaymentMethod());
        refund.setChargingSession(originalTransaction.getChargingSession());
        refund.setReservation(originalTransaction.getReservation());
        refund.setDriver(originalTransaction.getDriver());
        refund.setDescription("Refund for transaction #" + transactionId + ": " + reason);
        refund.setProcessedByStaffId(securityUtils.getCurrentStaffAccountId());
        refund.setInvoiceNumber(invoiceNumberGenerator.generate());
        refund.setPaidAt(OffsetDateTime.now());

        refund = transactionRepository.save(refund);

        log.info("Refund created: Transaction {} refunded by staff {}. Reason: {}",
                transactionId, securityUtils.getCurrentStaffAccountId(), reason);

        return mapToResponse(refund);
    }

    private void verifyStaffAccess(Transaction transaction) {
        Long stationId = null;

        if (transaction.getChargingSession() != null) {
            stationId = transaction.getChargingSession().getStation().getId();
        } else if (transaction.getReservation() != null) {
            stationId = transaction.getReservation().getStation().getId();
        }

        if (stationId == null) {
            throw new BusinessException("Cannot determine station for this transaction");
        }

        if (!staffStationService.hasAccessToStation(stationId)) {
            throw new BusinessException("You do not have access to this transaction");
        }
    }

    private TransactionDetailResponse mapToResponse(Transaction transaction) {
        TransactionDetailResponse response = new TransactionDetailResponse();
        response.setId(transaction.getId());
        response.setAmount(transaction.getAmount());
        response.setCurrency(transaction.getCurrency());
        response.setType(transaction.getType().name());
        response.setStatus(transaction.getStatus().name());
        response.setPaymentMethod(transaction.getPaymentMethod() != null
                ? transaction.getPaymentMethod().name() : null);
        response.setPaymentNotes(transaction.getPaymentNotes());
        response.setInvoiceNumber(transaction.getInvoiceNumber());
        response.setTimestamp(transaction.getTimestamp());
        response.setPaidAt(transaction.getPaidAt());
        response.setProcessedByStaffId(transaction.getProcessedByStaffId());

        if (transaction.getChargingSession() != null) {
            response.setChargingSessionId(transaction.getChargingSession().getId());
        }

        if (transaction.getReservation() != null) {
            response.setReservationId(transaction.getReservation().getId());
        }

        if (transaction.getDriver() != null) {
            response.setDriverId(transaction.getDriver().getId());
        }

        return response;
    }

}