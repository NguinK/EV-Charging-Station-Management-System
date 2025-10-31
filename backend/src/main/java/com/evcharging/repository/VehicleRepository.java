package com.evcharging.repository;

import com.evcharging.entity.Account;
import com.evcharging.entity.Vehicle;
import com.evcharging.enums.Role;
import com.evcharging.enums.AccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findByAccountId(Long accountId);
    Optional<Vehicle> findByLicensePlate(String licensePlate);
}