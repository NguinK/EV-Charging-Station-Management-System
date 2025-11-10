package com.evcharging.repository;

import com.evcharging.entity.WalletTransaction;
import com.evcharging.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {

    List<WalletTransaction> findByWalletId(Long walletId);

    @Query("SELECT wt FROM WalletTransaction wt WHERE wt.wallet.id = :walletId " +
            "ORDER BY wt.createdAt DESC")
    List<WalletTransaction> findByWalletIdOrderByCreatedAtDesc(@Param("walletId") Long walletId);

    @Query("SELECT wt FROM WalletTransaction wt WHERE wt.wallet.id = :walletId " +
            "AND wt.createdAt BETWEEN :startDate AND :endDate " +
            "ORDER BY wt.createdAt DESC")
    List<WalletTransaction> findByWalletIdAndDateRange(
            @Param("walletId") Long walletId,
            @Param("startDate") OffsetDateTime startDate,
            @Param("endDate") OffsetDateTime endDate);

    List<WalletTransaction> findByType(TransactionType type);

    Optional<WalletTransaction> findByReferenceId(String referenceId);
}
