package com.evcharging.controller;

import com.evcharging.entity.*;
import com.evcharging.enums.PaymentMethod;
import com.evcharging.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * Tạo payment cho phiên sạc
     * POST /api/payments/create
     */
    @PostMapping("/create")
    public ResponseEntity<Payment> createPayment(
            @RequestParam Long sessionId,
            @RequestParam PaymentMethod method) {

        Payment payment = paymentService.createPayment(sessionId, method);
        return ResponseEntity.ok(payment);
    }

    /**
     * Thanh toán qua ví điện tử
     * POST /api/payments/{paymentId}/wallet
     */
    @PostMapping("/{paymentId}/wallet")
    public ResponseEntity<Payment> payWithWallet(@PathVariable Long paymentId) {
        Payment payment = paymentService.processWalletPayment(paymentId);
        return ResponseEntity.ok(payment);
    }

    /**
     * Thanh toán online (banking, credit card)
     * POST /api/payments/{paymentId}/online
     */
    @PostMapping("/{paymentId}/online")
    public ResponseEntity<Payment> payOnline(
            @PathVariable Long paymentId,
            @RequestParam String gatewayTransactionId) {

        Payment payment = paymentService.processOnlinePayment(paymentId, gatewayTransactionId);
        return ResponseEntity.ok(payment);
    }

    /**
     * Thanh toán tiền mặt tại trạm
     * POST /api/payments/{paymentId}/cash
     */
    @PostMapping("/{paymentId}/cash")
    public ResponseEntity<Payment> payCash(
            @PathVariable Long paymentId,
            @RequestParam Long staffId) {

        Payment payment = paymentService.processCashPayment(paymentId, staffId);
        return ResponseEntity.ok(payment);
    }

    /**
     * Hoàn tiền
     * POST /api/payments/{paymentId}/refund
     */
    @PostMapping("/{paymentId}/refund")
    public ResponseEntity<Payment> refund(
            @PathVariable Long paymentId,
            @RequestParam String reason) {

        Payment payment = paymentService.refundPayment(paymentId, reason);
        return ResponseEntity.ok(payment);
    }
}