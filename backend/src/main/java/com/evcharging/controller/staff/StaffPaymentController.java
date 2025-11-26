package com.evcharging.controller.staff;

import com.evcharging.dto.ApiResponse;
import com.evcharging.dto.staff.RecordPaymentRequest;
import com.evcharging.dto.staff.TransactionDetailResponse;
import com.evcharging.service.staff.StaffPaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff/payments")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CS_STAFF')")
@Tag(name = "Staff Payment Management", description = "APIs for staff to process payments")
public class StaffPaymentController {
    private final StaffPaymentService staffPaymentService;

    @PostMapping("/transactions/{transactionId}/record")
    @Operation(summary = "Record payment",
            description = "Record cash/card/e-wallet payment for a pending transaction")
    public ResponseEntity<ApiResponse<TransactionDetailResponse>> recordPayment(
            @PathVariable Long transactionId,
            @Valid @RequestBody RecordPaymentRequest request) {

        TransactionDetailResponse response = staffPaymentService.recordPayment(transactionId, request);
        return ResponseEntity.ok(ApiResponse.success("Payment recorded successfully", response));
    }

    @GetMapping("/transactions/{transactionId}")
    @Operation(summary = "Get transaction detail")
    public ResponseEntity<ApiResponse<TransactionDetailResponse>> getTransactionDetail(
            @PathVariable Long transactionId) {

        TransactionDetailResponse response = staffPaymentService.getTransactionDetail(transactionId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/stations/{stationId}/pending-transactions")
    @Operation(summary = "Get pending transactions",
            description = "Get all pending transactions for a station")
    public ResponseEntity<ApiResponse<List<TransactionDetailResponse>>> getPendingTransactions(
            @PathVariable Long stationId) {

        List<TransactionDetailResponse> transactions = staffPaymentService.getPendingTransactions(stationId);
        return ResponseEntity.ok(ApiResponse.success("Pending transactions retrieved", transactions));
    }

    @PostMapping("/transactions/{transactionId}/refund")
    @Operation(summary = "Refund transaction",
            description = "Create a refund for a successful transaction")
    public ResponseEntity<ApiResponse<TransactionDetailResponse>> refundTransaction(
            @PathVariable Long transactionId,
            @RequestParam String reason) {

        TransactionDetailResponse response = staffPaymentService.refundTransaction(transactionId, reason);
        return ResponseEntity.ok(ApiResponse.success("Refund processed successfully", response));
    }
}
