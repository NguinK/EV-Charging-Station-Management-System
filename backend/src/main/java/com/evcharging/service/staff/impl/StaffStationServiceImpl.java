package com.evcharging.service.staff.impl;

import com.evcharging.dto.staff.StationSummaryResponse;
import com.evcharging.entity.ChargingPoint;
import com.evcharging.entity.StaffAssignment;
import com.evcharging.enums.ChargingPointStatus;
import com.evcharging.repository.ChargingPointRepository;
import com.evcharging.repository.StaffAssignmentRepository;
import com.evcharging.service.staff.StaffStationService;
import com.evcharging.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StaffStationServiceImpl implements StaffStationService {
    private final StaffAssignmentRepository staffAssignmentRepository;
    private final ChargingPointRepository chargingPointRepository;
    private final SecurityUtils securityUtils;

    @Override
    @Transactional(readOnly = true)
    public List<StationSummaryResponse> getMyStations() {
        Long staffId = securityUtils.getCurrentStaffAccountId();

        List<StaffAssignment> assignments = staffAssignmentRepository
                .findActiveAssignmentsByStaffAccountId(staffId);

        return assignments.stream()
                .map(assignment -> {
                    StationSummaryResponse response = new StationSummaryResponse();
                    response.setId(assignment.getStation().getId());
                    response.setName(assignment.getStation().getName());
                    response.setLocation(assignment.getStation().getLocation());
                    response.setStatus(assignment.getStation().getStatus().name());

                    // Get charger statistics
                    List<ChargingPoint> chargingPoints =
                            chargingPointRepository.findByStationId(assignment.getStation().getId());

                    response.setTotalChargers(chargingPoints.size());
                    response.setAvailableChargers((int) chargingPoints.stream()
                            .filter(cp -> cp.getStatus() == ChargingPointStatus.AVAILABLE)
                            .count());
                    response.setInUseChargers((int) chargingPoints.stream()
                            .filter(cp -> cp.getStatus() == ChargingPointStatus.OCCUPIED)
                            .count());

                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasAccessToStation(Long stationId) {
        Long staffId = securityUtils.getCurrentStaffAccountId();
        return staffAssignmentRepository.existsByStaffAccountIdAndStationIdAndActiveTrue(staffId, stationId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> getAccessibleStationIds() {
        Long staffId = securityUtils.getCurrentStaffAccountId();
        return staffAssignmentRepository.findStationIdsByStaffAccountId(staffId);
    }
}
