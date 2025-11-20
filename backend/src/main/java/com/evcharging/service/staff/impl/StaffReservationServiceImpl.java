package com.evcharging.service.staff.impl;

import com.evcharging.dto.staff.CheckInReservationRequest;
import com.evcharging.dto.staff.ReservationSummaryResponse;
import com.evcharging.entity.ChargingPoint;
import com.evcharging.entity.Reservation;
import com.evcharging.entity.Transaction;
import com.evcharging.enums.ChargingPointStatus;
import com.evcharging.enums.ReservationStatus;
import com.evcharging.enums.TransactionStatus;
import com.evcharging.enums.TransactionType;
import com.evcharging.exception.BusinessException;
import com.evcharging.exception.ResourceNotFoundException;
import com.evcharging.repository.ChargingPointRepository;
import com.evcharging.repository.ReservationRepository;
import com.evcharging.repository.TransactionRepository;
import com.evcharging.service.PricingService;
import com.evcharging.service.staff.StaffReservationService;
import com.evcharging.service.staff.StaffStationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StaffReservationServiceImpl implements StaffReservationService {

    private final ReservationRepository reservationRepository;
    private final ChargingPointRepository chargingPointRepository;
    private final TransactionRepository transactionRepository;
    private final StaffStationService staffStationService;
    private final PricingService pricingService;

    @Override
    @Transactional(readOnly = true)
    public List<ReservationSummaryResponse> getTodayReservations(Long stationId) {
        // Verify staff has access to this station
        if (!staffStationService.hasAccessToStation(stationId)) {
            throw new BusinessException("You do not have access to this station");
        }

        List<ReservationStatus> activeStatuses = Arrays.asList(
                ReservationStatus.CONFIRMED,
                ReservationStatus.CHECKED_IN
        );

        List<Reservation> reservations = reservationRepository.findByStationIdAndStatusInAndDate(
                stationId, activeStatuses, OffsetDateTime.now()
        );

        return reservations.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ReservationSummaryResponse checkInReservation(Long reservationId, CheckInReservationRequest request) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));

        // Verify staff has access to the station
        if (!staffStationService.hasAccessToStation(reservation.getStation().getId())) {
            throw new BusinessException("You do not have access to this station");
        }

        // Validate reservation status
        if (reservation.getStatus() != ReservationStatus.CONFIRMED) {
            throw new BusinessException("Only CONFIRMED reservations can be checked in");
        }

        // Assign charger if needed
        if (reservation.getChargingPoint() == null && request.getChargerId() != null) {
            ChargingPoint chargingPoint = chargingPointRepository.findById(request.getChargerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Charger not found"));

            if (!chargingPoint.getStation().getId().equals(reservation.getStation().getId())) {
                throw new BusinessException("Charger does not belong to the reservation station");
            }

            if (chargingPoint.getStatus() != ChargingPointStatus.AVAILABLE) {
                throw new BusinessException("Charger is not available");
            }

            reservation.setChargingPoint(chargingPoint);
            chargingPoint.setStatus(ChargingPointStatus.OCCUPIED);
            chargingPointRepository.save(chargingPoint);
        } else if (reservation.getChargingPoint() != null) {
            // Update existing charger status
            ChargingPoint chargingPoint = reservation.getChargingPoint();
            chargingPoint.setStatus(ChargingPointStatus.OCCUPIED);
            chargingPointRepository.save(chargingPoint);
        } else {
            throw new BusinessException("Charger ID must be provided");
        }

        reservation.setStatus(ReservationStatus.CHECKED_IN);
        reservation = reservationRepository.save(reservation);

        return mapToResponse(reservation);
    }

    @Override
    @Transactional
    public ReservationSummaryResponse markAsNoShow(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));

        // Verify staff has access to the station
        if (!staffStationService.hasAccessToStation(reservation.getStation().getId())) {
            throw new BusinessException("You do not have access to this station");
        }

        // Validate reservation status
        if (reservation.getStatus() != ReservationStatus.CONFIRMED) {
            throw new BusinessException("Only BOOKED reservations can be marked as no-show");
        }

        // Update reservation status
        reservation.setStatus(ReservationStatus.NO_SHOW);
        reservation = reservationRepository.save(reservation);

        // Create penalty transaction
        Transaction penalty = new Transaction();
        penalty.setTimestamp(OffsetDateTime.now());
        penalty.setAmount(pricingService.getPenaltyAmount());
        penalty.setCurrency("VND");
        penalty.setType(TransactionType.PENALTY);
        penalty.setStatus(TransactionStatus.PENDING);
        penalty.setReservation(reservation);
        penalty.setDriver(reservation.getDriver());
        penalty.setDescription("No-show penalty for reservation #" + reservationId);
        transactionRepository.save(penalty);

        return mapToResponse(reservation);
    }

    private ReservationSummaryResponse mapToResponse(Reservation reservation) {
        ReservationSummaryResponse response = new ReservationSummaryResponse();
        response.setId(reservation.getId());
        response.setDriverId(reservation.getDriver().getId());
        response.setDriverName(reservation.getDriver().getFullName());
        response.setDriverPhone(reservation.getDriver().getPhone());
        response.setStationId(reservation.getStation().getId());
        response.setStationName(reservation.getStation().getName());

        if (reservation.getChargingPoint() != null) {
            response.setChargerId(reservation.getChargingPoint().getId());
            response.setChargerCode(reservation.getChargingPoint().getPointCode());
        }

        response.setStartTime(reservation.getStartTime());
        response.setEndTime(reservation.getExpireTime());
        response.setStatus(reservation.getStatus().name());
        response.setHoldFee(reservation.getHoldingFee());

        return response;
    }
}
