package com.evcharging.repository;

import com.evcharging.entity.ChargingStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChargingStationRepository extends JpaRepository<ChargingStation, Long> {
    // Ví dụ: tìm theo tên
    List<ChargingStation> findByNameContainingIgnoreCase(String name);

    // Ví dụ: tìm theo trạng thái
    List<ChargingStation> findByStatus(String status);
}