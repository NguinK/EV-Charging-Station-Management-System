package com.evcharging.repository;

import com.evcharging.entity.CS_Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CS_StaffRepository extends JpaRepository<CS_Staff, Long> {
    Optional<CS_Staff> findByAccountId(Long id);
}
