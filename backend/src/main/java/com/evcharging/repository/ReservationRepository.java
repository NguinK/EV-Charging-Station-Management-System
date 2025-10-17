package com.evcharging.repository;

import com.evcharging.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByDriverId(Long driverId);
    List<Reservation> findByStationId(Long stationId);
}