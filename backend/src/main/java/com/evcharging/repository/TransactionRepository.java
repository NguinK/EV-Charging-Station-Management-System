package com.evcharging.repository;

import com.evcharging.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // Lấy danh sách giao dịch của 1 driver trong khoảng thời gian
    List<Transaction> findByDriverIdAndTimestampBetween(
            Long driverId,
            OffsetDateTime from,
            OffsetDateTime to
    );

    List<Transaction> findAllByDriverId(Long driverId);

    Optional<Transaction> findBySessionId(Long sessionId);

    Optional<Transaction> findByInvoiceNumber(String invoiceNumber);

}