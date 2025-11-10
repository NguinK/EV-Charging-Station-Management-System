package com.evcharging.repository;

import com.evcharging.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByPhone(String phone);

    Optional<Account> findByEmail(String email);

    Optional<Account> findById(Long Id);
}