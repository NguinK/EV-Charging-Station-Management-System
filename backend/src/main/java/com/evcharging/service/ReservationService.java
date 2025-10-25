package com.evcharging.service;

import com.evcharging.dto.ReservationCreateDTO;
import com.evcharging.dto.ReservationResponseDTO;
import com.evcharging.entity.ChargingPoint;
import com.evcharging.entity.ChargingStation;
import com.evcharging.entity.EVDriver;
import com.evcharging.entity.Reservation;
import com.evcharging.enums.ReservationStatus;
import com.evcharging.repository.ChargingPointRepository;
import com.evcharging.repository.ChargingStationRepository;
import com.evcharging.repository.EVDriverRepository;
import com.evcharging.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final EVDriverRepository driverRepository;
    private final ChargingStationRepository stationRepository;
    private final ChargingPointRepository chargingPointRepository;
    public ReservationService(ReservationRepository reservationRepository,
                              EVDriverRepository driverRepository,
                              ChargingStationRepository stationRepository,
                              ChargingPointRepository chargingPointRepository) {
        this.reservationRepository = reservationRepository;
        this.driverRepository = driverRepository;
        this.stationRepository = stationRepository;
        this.chargingPointRepository=chargingPointRepository;
    }

    @Transactional
    public ReservationResponseDTO createReservationAutoApprove(Long driverId, ReservationCreateDTO dto) {
        EVDriver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        ChargingStation station = stationRepository.findById(dto.getStationId())
                .orElseThrow(() -> new RuntimeException("Station not found"));

        LocalDateTime startTime = dto.getStartTime();
        LocalDateTime expireTime = startTime.plusMinutes(30);

        List<ChargingPoint> stationPoints = chargingPointRepository.findByStationId(
                dto.getStationId());
        List<ChargingPoint> filteredPoints = stationPoints.stream()
                .filter(p -> p.getConnectorType().equals(dto.getConnectorType()))
                .collect(Collectors.toList());

        for (ChargingPoint point : stationPoints) {
            boolean isOccupied = reservationRepository.existsByChargingPointAndStatusInAndTimeOverlap(
                    point,
                    List.of(ReservationStatus.CONFIRMED),
                    startTime,
                    expireTime
            );

            if (!isOccupied) {
                Reservation reservation = new Reservation();
                reservation.setDriver(driver);
                reservation.setChargingPoint(point);
                reservation.setStation(station);
                reservation.setConnectorType(dto.getConnectorType());
                reservation.setStartTime(startTime);
                reservation.setExpireTime(expireTime);
                reservation.setStatus(ReservationStatus.CONFIRMED);

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
        }

        throw new RuntimeException("Không còn điểm sạc nào trống tại thời điểm này");
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
        reservation.setExpireTime(dto.getStartTime().plusMinutes(30));

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

    public ReservationResponseDTO getReservationDetails(Long driverId) {

        Reservation res = reservationRepository.findById(driverId)
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

    public List<ReservationResponseDTO> getReservationList(Long accountId) {
        EVDriver driver = driverRepository.findByAccountId(accountId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        List<Reservation> reservations = reservationRepository.findByDriver(driver);

        return reservations.stream()
                .map(res -> new ReservationResponseDTO(
                        res.getId(),
                        res.getStation().getName(),
                        res.getConnectorType(),
                        res.getStatus(),
                        res.getStartTime(),
                        res.getExpireTime()
                ))
                .collect(Collectors.toList());

    }
}