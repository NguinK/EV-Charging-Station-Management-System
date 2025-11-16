package com.evcharging.service.staff.impl;

import com.evcharging.dto.staff.ChargingSessionResponse;
import com.evcharging.dto.staff.StartSessionRequest;
import com.evcharging.dto.staff.StopSessionRequest;
import com.evcharging.entity.*;
import com.evcharging.enums.*;
import com.evcharging.exception.BusinessException;
import com.evcharging.exception.ResourceNotFoundException;
import com.evcharging.repository.*;
import com.evcharging.service.PricingService;
import com.evcharging.service.staff.StaffChargingSessionService;
import com.evcharging.service.staff.StaffStationService;
import com.evcharging.utils.InvoiceNumberGenerator;
import com.evcharging.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class StaffChargingSessionServiceImpl implements StaffChargingSessionService {

    private final ChargingSessionRepository chargingSessionRepository;
    private final ReservationRepository reservationRepository;
    private final ChargingStationRepository chargingStationRepository;
    private final ChargingPointRepository chargingPointRepository;
    private final TransactionRepository transactionRepository;
    private final StaffStationService staffStationService;
    private final SecurityUtils securityUtils;
    private final PricingService pricingService;
    private final EVDriverRepository evDriverRepository;

    public ChargingSessionResponse startSession(StartSessionRequest request) {
        //Verify staff has access to the station
        if (!staffStationService.hasAccessToStation(request.getStationId())) {
            throw new BusinessException("You do not have access to this station");
        }

        // Validate station
        ChargingStation station = chargingStationRepository.findById(request.getStationId())
                .orElseThrow(() -> new ResourceNotFoundException("Station not found"));

        // Validate charging point
        ChargingPoint chargingPoint = chargingPointRepository
                .findByStationIdAndId(request.getStationId(), request.getChargingPointId())
                .orElseThrow(() -> new ResourceNotFoundException("Charging point not found or doesn't belong to this station"));

//        ChargingPoint chargingPoint = chargingPointRepository
//                .findByIdAndStationId(request.getChargingPointId(), request.getStationId())
//                .orElseThrow(() -> new ResourceNotFoundException("Charging point not found or doesn't belong to this station"));

        if (chargingPoint.getStatus() == ChargingPointStatus.MAINTENANCE) {
            throw new BusinessException("Charging point is out of service");
        }

        if (chargingPoint.getStatus() == ChargingPointStatus.OCCUPIED) {
            throw new BusinessException("Charging point is already in use");
        }

        // Validate reservation if provided
        Reservation reservation = null;
        if (request.getReservationId() != null) {
            reservation = reservationRepository.findById(request.getReservationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));

            if (reservation.getStatus() != ReservationStatus.CHECKED_IN) {
                throw new BusinessException("Reservation must be checked in first");
            }
        }

        EVDriver driver = evDriverRepository.findById(request.getDriverId())
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found"));

        //Create charging session
        ChargingSession session = new ChargingSession();
        session.setReservation(reservation);
        session.setStation(station);
        session.setChargingPoint(chargingPoint);
        session.setDriver(driver);
        session.setStartTime(OffsetDateTime.now());
        session.setStatus(SessionStatus.CHARGING);
        session.setStartedByStaffId(securityUtils.getCurrentStaffAccountId());
        session = chargingSessionRepository.save(session);

        // Update charger status
        chargingPoint.setStatus(ChargingPointStatus.OCCUPIED);
        chargingPointRepository.save(chargingPoint);

        return mapToResponse(session);
    }

    @Override
    @Transactional
    public ChargingSessionResponse stopSession(Long sessionId, StopSessionRequest request) {
        ChargingSession session = chargingSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Charging session not found"));

        // Verify staff has access to the station
        if (!staffStationService.hasAccessToStation(session.getStation().getId())) {
            throw new BusinessException("You do not have access to this station");
        }

        // Validate session status
        if (session.getStatus() != SessionStatus.CHARGING) {
            throw new BusinessException("Only running sessions can be stopped");
        }

        // Update session
        session.setEndTime(OffsetDateTime.now());
        session.setEnergyConsumed(request.getEnergyUsedKwh());
        session.setStatus(SessionStatus.COMPLETED);
        session.setEndedByStaffId(securityUtils.getCurrentStaffAccountId());

        session = chargingSessionRepository.save(session);

        // Update charger status
        ChargingPoint chargingPoint = session.getChargingPoint();
        chargingPoint.setStatus(ChargingPointStatus.AVAILABLE);
        chargingPointRepository.save(chargingPoint);

        // Update reservation if exists
        if (session.getReservation() != null) {
            Reservation reservation = session.getReservation();
            reservation.setStatus(ReservationStatus.COMPLETED);
            reservationRepository.save(reservation);
        }

        // Calculate cost and create transaction
        BigDecimal chargingCost = pricingService.calculateChargingFee(session);

        Transaction transaction = new Transaction();
        transaction.setTimestamp(OffsetDateTime.now());
        transaction.setAmount(chargingCost);
        transaction.setCurrency("VND");
        transaction.setType(TransactionType.PAYMENT);
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setChargingSession(session);
        transaction.setDriver(session.getDriver());
        transaction.setDescription(String.format("Charging payment for %.2f kWh", request.getEnergyUsedKwh()));

        if (request.getPaymentMethod() != null) {
            try {
                PaymentMethod method = PaymentMethod.valueOf(
                        request.getPaymentMethod().toUpperCase()
                );
                transaction.setPaymentMethod(method);
                transaction.setStatus(TransactionStatus.SUCCESS);
                transaction.setPaidAt(OffsetDateTime.now());
                transaction.setProcessedByStaffId(securityUtils.getCurrentStaffAccountId());
                transaction.setInvoiceNumber(InvoiceNumberGenerator.generate());
            } catch (IllegalArgumentException e) {
                // Nếu payment method không hợp lệ, để PENDING
                log.warn("Invalid payment method: {}", request.getPaymentMethod());
            }
        }

        transactionRepository.save(transaction);

        return mapToResponse(session);
    }

    private ChargingSessionResponse mapToResponse(ChargingSession session) {
        ChargingSessionResponse response = new ChargingSessionResponse();
        response.setId(session.getId());

        if(session.getReservation() != null) {
            response.setReservationId(session.getReservation().getId());
        }

        response.setStationId(session.getStation().getId());
        response.setStationName(session.getStation().getName());
        response.setChargerId(session.getChargingPoint().getId());
        response.setChargerCode(session.getChargingPoint().getPointCode());
        response.setDriverId(session.getId());
        response.setStartTime(session.getStartTime());
        response.setEndTime(session.getEndTime());
        response.setEnergyUsedKwh(session.getEnergyConsumed());
        response.setStatus(session.getStatus().name());
        response.setStartedByStaffId(session.getStartedByStaffId());
        response.setEndedByStaffId(session.getEndedByStaffId());

        return response;
    }
}
