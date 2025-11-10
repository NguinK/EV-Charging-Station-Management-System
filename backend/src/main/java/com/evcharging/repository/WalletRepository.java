package com.evcharging.repository;

import com.evcharging.entity.Wallet;
import com.evcharging.enums.WalletStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    Optional<Wallet> findByAccountId(Long accountId);

    List<Wallet> findByStatus(WalletStatus status);
}
