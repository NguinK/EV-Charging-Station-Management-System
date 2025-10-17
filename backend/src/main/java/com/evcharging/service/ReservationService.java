package com.evcharging.service;

import com.evcharging.dto.ReservationCreateDTO;
import com.evcharging.dto.ReservationResponseDTO;
import com.evcharging.entity.ChargingStation;
import com.evcharging.entity.EVDriver;
import com.evcharging.entity.Reservation;
import com.evcharging.enums.ReservationStatus;
import com.evcharging.repository.ChargingStationRepository;
import com.evcharging.repository.EVDriverRepository;
import com.evcharging.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final EVDriverRepository driverRepository;
    private final ChargingStationRepository stationRepository;

    public ReservationService(ReservationRepository reservationRepository,
                              EVDriverRepository driverRepository,
                              ChargingStationRepository stationRepository) {
        this.reservationRepository = reservationRepository;
        this.driverRepository = driverRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public ReservationResponseDTO createReservation(Long driverId, ReservationCreateDTO dto) {
        EVDriver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));
        ChargingStation station = stationRepository.findById(dto.getStationId())
                .orElseThrow(() -> new RuntimeException("Station not found"));

        Reservation reservation = new Reservation();
        reservation.setDriver(driver);
        reservation.setStation(station);
        reservation.setConnectorType(dto.getConnectorType());
        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservation.setStartTime(dto.getStartTime());
        reservation.setExpireTime(dto.getStartTime().plusMinutes(15));

        Reservation saved = reservationRepository.save(reservation);

        return new ReservationResponseDTO(
                saved.getId(),
                station.getName(),
                saved.getConnectorType(),
                saved.getStatus(),
                saved.getStartTime(),
                saved.getExpireTime()
        );
    }

    public ReservationResponseDTO getReservation(Long id) {
        Reservation res = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
        return new ReservationResponseDTO(
                res.getId(),
                res.getStation().getName(),
                res.getConnectorType(),
                res.getStatus(),
                res.getStartTime(),
                res.getExpireTime()
        );
    }

    @Transactional
    public void cancelReservation(Long id) {
        Reservation res = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
        res.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(res);
    }

    public List<ReservationResponseDTO> getReservationsByDriver(Long driverId) {
        List<Reservation> reservations = reservationRepository.findByDriverId(driverId);
        return reservations.stream()
                .map(res -> new ReservationResponseDTO(
                        res.getId(),
                        res.getStation().getName(),
                        res.getConnectorType(),
                        res.getStatus(),
                        res.getStartTime(),
                        res.getExpireTime()
                ))
                .toList();
    }
}