package com.evcharging.repository;

import com.evcharging.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    Optional<Invoice> findByTransactionId(Long transactionId);

    boolean existsByTransactionId(Long transactionId);

    @Query("SELECT i FROM Invoice i WHERE i.transaction.driver.id = :driverId ORDER BY i.createdAt DESC")
    List<Invoice> findByDriverIdOrderByCreatedAtDesc(@Param("driverId") Long driverId);

    List<Invoice> findByStationIdAndCreatedAtBetween(
            Long stationId,
            OffsetDateTime startDate,
            OffsetDateTime endDate
    );

    @Query("SELECT SUM(i.finalAmount) FROM Invoice i " +
            "WHERE i.stationId = :stationId " +
            "AND i.createdAt BETWEEN :startDate AND :endDate " +
            "AND i.status = 'ISSUED'")
    Double sumFinalAmountByStationAndDateRange(
            @Param("stationId") Long stationId,
            @Param("startDate") OffsetDateTime startDate,
            @Param("endDate") OffsetDateTime endDate
    );
}


