package com.evcharging.service;

package com.evcharging.service;

import com.evcharging.entity.*;
import com.evcharging.enums.*;
import com.evcharging.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepo;
    private final ChargingSessionRepository sessionRepo;
    private final WalletService walletService;
    private final UserSubscriptionRepository subscriptionRepo;
    private final InvoiceService invoiceService;

    /**
     * Tạo payment cho phiên sạc
     */
    @Transactional
    public Payment createPayment(Long sessionId, PaymentMethod method) {
        log.info("Creating payment for session: {}, method: {}", sessionId, method);

        ChargingSession session = sessionRepo.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        if (session.getStatus() != SessionStatus.COMPLETED) {
            throw new RuntimeException("Can only create payment for completed sessions");
        }

        // Kiểm tra đã có payment chưa
        if (paymentRepo.findBySessionId(sessionId).isPresent()) {
            throw new RuntimeException("Payment already exists for this session");
        }

        // Tính toán chi phí
        ChargingPoint point = session.getChargingPoint();
        double energyCost = session.getEnergyConsumed() * point.getPricePerKwh();
        double timeCost = session.getDuration() * point.getPricePerMinute();
        double totalAmount = energyCost + timeCost;

        // Áp dụng discount từ subscription (nếu có)
        double discount = calculateDiscount(session.getAccount().getId(), totalAmount);
        double finalAmount = totalAmount - discount;

        // Tạo payment
        Payment payment = new Payment();
        payment.setSession(session);
        payment.setAccount(session.getAccount());
        payment.setAmount(totalAmount);
        payment.setEnergyCost(energyCost);
        payment.setTimeCost(timeCost);
        payment.setDiscount(discount);
        payment.setFinalAmount(finalAmount);
        payment.setMethod(method);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setInvoiceNumber(generateInvoiceNumber());
        payment.setTransactionId(generateTransactionId());

        payment = paymentRepo.save(payment);

        log.info("Payment created: {}, Amount: {} VND", payment.getId(), finalAmount);

        return payment;
    }

    /**
     * Xử lý thanh toán qua ví điện tử
     */
    @Transactional
    public Payment processWalletPayment(Long paymentId) {
        log.info("Processing wallet payment: {}", paymentId);

        Payment payment = paymentRepo.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new RuntimeException("Payment is not in pending status");
        }

        if (payment.getMethod() != PaymentMethod.EWALLET) {
            throw new RuntimeException("Payment method is not E_WALLET");
        }

        try {
            // Trừ tiền từ ví
            walletService.deductBalance(
                    payment.getAccount().getId(),
                    payment.getFinalAmount(),
                    "Payment for session #" + payment.getSession().getId());

            // Cập nhật trạng thái payment
            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setPaymentTime(LocalDateTime.now());
            payment = paymentRepo.save(payment);

            // Tạo hóa đơn điện tử
            invoiceService.generateInvoice(payment);

            log.info("Wallet payment completed: {}", paymentId);

            return payment;

        } catch (Exception e) {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepo.save(payment);
            log.error("Wallet payment failed: {}", e.getMessage());
            throw new RuntimeException("Payment failed: " + e.getMessage());
        }
    }

    /**
     * Xử lý thanh toán online (banking, credit card)
     */
    @Transactional
    public Payment processOnlinePayment(Long paymentId, String gatewayTransactionId) {
        log.info("Processing online payment: {}, Gateway TxID: {}", paymentId, gatewayTransactionId);

        Payment payment = paymentRepo.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        payment.setStatus(PaymentStatus.PROCESSING);
        paymentRepo.save(payment);

        try {
            // TODO: Tích hợp với payment gateway (VNPay, Momo, ZaloPay...)
            boolean paymentSuccess = verifyPaymentGateway(gatewayTransactionId);

            if (paymentSuccess) {
                payment.setStatus(PaymentStatus.COMPLETED);
                payment.setPaymentTime(LocalDateTime.now());
                payment.setTransactionId(gatewayTransactionId);
                payment = paymentRepo.save(payment);

                // Tạo hóa đơn
                invoiceService.generateInvoice(payment);

                log.info("Online payment completed: {}", paymentId);
            } else {
                payment.setStatus(PaymentStatus.FAILED);
                paymentRepo.save(payment);
                throw new RuntimeException("Payment verification failed");
            }

            return payment;

        } catch (Exception e) {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepo.save(payment);
            log.error("Online payment failed: {}", e.getMessage());
            throw new RuntimeException("Payment failed: " + e.getMessage());
        }
    }

    /**
     * Xử lý thanh toán tiền mặt tại trạm
     */
    @Transactional
    public Payment processCashPayment(Long paymentId, Long staffId) {
        log.info("Processing cash payment: {} by staff: {}", paymentId, staffId);

        Payment payment = paymentRepo.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setPaymentTime(LocalDateTime.now());
        payment = paymentRepo.save(payment);

        // Tạo hóa đơn
        invoiceService.generateInvoice(payment);

        log.info("Cash payment completed: {}", paymentId);

        return payment;
    }

    /**
     * Hoàn tiền
     */
    @Transactional
    public Payment refundPayment(Long paymentId, String reason) {
        log.info("Processing refund for payment: {}, Reason: {}", paymentId, reason);

        Payment payment = paymentRepo.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new RuntimeException("Can only refund completed payments");
        }

        // Hoàn tiền vào ví nếu thanh toán qua ví
        if (payment.getMethod() == PaymentMethod.EWALLET) {
            walletService.addBalance(
                    payment.getAccount().getId(),
                    payment.getFinalAmount(),
                    "Refund for session #" + payment.getSession().getId());
        }

        payment.setStatus(PaymentStatus.REFUNDED);
        payment = paymentRepo.save(payment);

        log.info("Payment refunded: {}", paymentId);

        return payment;
    }

    /**
     * Tính discount từ subscription
     */
    private double calculateDiscount(Long accountId, double amount) {
        return subscriptionRepo.findActiveSubscription(accountId, LocalDateTime.now())
                .map(subscription -> {
                    double discountRate = subscription.getPlan().getDiscountRate();
                    return amount * (discountRate / 100);
                })
                .orElse(0.0);
    }

    /**
     * Xác minh thanh toán từ payment gateway
     */
    private boolean verifyPaymentGateway(String transactionId) {
        // TODO: Implement actual payment gateway verification
        return true; // Giả lập thành công
    }

    /**
     * Generate invoice number
     */
    private String generateInvoiceNumber() {
        return "INV-" + LocalDateTime.now().getYear() +
                String.format("%08d", System.currentTimeMillis() % 100000000);
    }

    /**
     * Generate transaction ID
     */
    private String generateTransactionId() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
    }
}