package com.evcharging.repository;

import com.evcharging.entity.EVDriver;
import com.evcharging.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByStationId(Long stationId);
    List<Reservation> findByDriver(EVDriver driver);
}