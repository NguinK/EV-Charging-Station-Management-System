package com.evcharging.repository;

import com.evcharging.entity.Payment;
import com.evcharging.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByAccountId(Long accountId);

    List<Payment> findByStatus(PaymentStatus status);

    Optional<Payment> findBySessionId(Long sessionId);

    Optional<Payment> findByTransactionId(String transactionId);

    Optional<Payment> findByInvoiceNumber(String invoiceNumber);

    @Query("SELECT SUM(p.finalAmount) FROM Payment p " +
            "WHERE p.status = 'COMPLETED' " +
            "AND p.paymentTime BETWEEN :startDate AND :endDate")
    Double calculateTotalRevenue(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}
