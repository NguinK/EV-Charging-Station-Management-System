package com.evcharging.repository;


import com.evcharging.entity.EVDriver;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EVDriverRepository extends JpaRepository<EVDriver, Long> {
    Optional<EVDriver> findByAccountId(Long accountId);
}