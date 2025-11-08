package com.evcharging.service.staff.impl;

import com.evcharging.entity.ChargingPoint;
import com.evcharging.enums.ChargingPointStatus;
import com.evcharging.exception.BusinessException;
import com.evcharging.exception.ResourceNotFoundException;
import com.evcharging.repository.ChargingPointRepository;
import com.evcharging.service.staff.StaffChargingPointService;
import com.evcharging.service.staff.StaffStationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class StaffChargingPointServiceImpl implements StaffChargingPointService {

    private final ChargingPointRepository chargingPointRepository;
    private final StaffStationService staffStationService;

    @Override
    @Transactional
    public void setChargingPointAvailable(Long chargingPointId) {
        ChargingPoint chargingPoint = chargingPointRepository.findById(chargingPointId)
                .orElseThrow(() -> new ResourceNotFoundException("Charger not found"));

        // Verify staff has access to the station
        if (!staffStationService.hasAccessToStation(chargingPoint.getStation().getId())) {
            throw new BusinessException("You do not have access to this station");
        }

        if (chargingPoint.getStatus() == ChargingPointStatus.OCCUPIED) {
            throw new BusinessException("Cannot set charger to available while in use");
        }

        chargingPoint.setStatus(ChargingPointStatus.AVAILABLE);
        chargingPointRepository.save(chargingPoint);

        log.info("Charger {} set to AVAILABLE by staff", chargingPointId);
    }

    @Override
    @Transactional
    public void setChargerOutOfService(Long chargerId, String reason) {
        ChargingPoint chargingPoint = chargingPointRepository.findById(chargerId)
                .orElseThrow(() -> new ResourceNotFoundException("Charger not found"));

        // Verify staff has access to the station
        if (!staffStationService.hasAccessToStation(chargingPoint.getStation().getId())) {
            throw new BusinessException("You do not have access to this station");
        }

        if (chargingPoint.getStatus() == ChargingPointStatus.OCCUPIED) {
            throw new BusinessException("Cannot set charging point maintenance while in use. Stop the session first.");
        }

        chargingPoint.setStatus(ChargingPointStatus.MAINTENANCE);
        chargingPointRepository.save(chargingPoint);

        log.info("Charger {} set to OUT_OF_SERVICE by staff. Reason: {}", chargerId, reason);
    }
}
