package com.evcharging.service.admin;

import com.evcharging.dto.admin.CreateStaffRequest;
import com.evcharging.dto.admin.StaffResponse;
import com.evcharging.entity.Account;
import com.evcharging.entity.ChargingStation;
import com.evcharging.entity.StaffAssignment;
import com.evcharging.enums.AccountStatus;
import com.evcharging.enums.Role;
import com.evcharging.repository.AccountRepository;
import com.evcharging.repository.ChargingStationRepository;
import com.evcharging.repository.StaffAssignmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminStaffService {
    private final AccountRepository accountRepository;
    private final ChargingStationRepository stationRepository;
    private final StaffAssignmentRepository staffAssignmentRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String PASSWORD_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
    private static final int PASSWORD_LENGTH = 12;

    //Create a new staff user with station assignments
    @Transactional
    public StaffResponse createStaff(CreateStaffRequest request) {
        log.info("Creating staff user with email: {}", request.getEmail());

        // 1. Validate email is unique
        if (accountRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + request.getEmail());
        }

        // 2. Validate all stations exist
        List<ChargingStation> stations = stationRepository.findAllById(request.getStationIds());
        if (stations.size() != request.getStationIds().size()) {
            throw new IllegalArgumentException("One or more station IDs are invalid");
        }

        // 3. Generate or use provided password
        String rawPassword = request.getPassword() != null && !request.getPassword().isBlank()
                ? request.getPassword()
                : generateSecurePassword();

        // 4. Create Account entity
        Account staffAccount = Account.builder()
                .email(request.getEmail())
                .phone(request.getPhone())
                .fullName(request.getFullName())
                .password(passwordEncoder.encode(rawPassword))
                .role(Role.CS_STAFF)
                .status(AccountStatus.ACTIVE)
                .build();

        staffAccount = accountRepository.save(staffAccount);
        log.info("Staff account created with ID: {}", staffAccount.getId());

        // 5. Create StaffAssignment entries for each station
        Long staffAccountId = staffAccount.getId();
        OffsetDateTime now = OffsetDateTime.now();

        List<StaffAssignment> assignments = stations.stream()
                .map(station -> {
                    StaffAssignment assignment = new StaffAssignment();
                    assignment.setStaffAccountId(staffAccountId);
                    assignment.setStation(station);
                    assignment.setAssignedAt(now);
                    assignment.setActive(true);
                    return assignment;
                })
                .collect(Collectors.toList());

        staffAssignmentRepository.saveAll(assignments);
        log.info("Created {} station assignments for staff {}", assignments.size(), staffAccountId);

        // 6. Build response
        return buildStaffResponse(staffAccount, assignments, rawPassword);
    }

    //Get all staff users with pagination
    public Page<StaffResponse> getAllStaff(int page, int size) {
        log.info("Getting all staff users - page: {}, size: {}", page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Account> staffAccounts = accountRepository.findByRole(Role.CS_STAFF, pageable);

        return staffAccounts.map(account -> {
            List<StaffAssignment> assignments = staffAssignmentRepository
                    .findActiveAssignmentsByStaffAccountId(account.getId());
            return buildStaffResponse(account, assignments, null);
        });
    }

    //Get staff details by ID
    public StaffResponse getStaffById(Long staffId) {
        log.info("Getting staff details for ID: {}", staffId);

        Account staffAccount = accountRepository.findById(staffId)
                .orElseThrow(() -> new IllegalArgumentException("Staff not found with ID: " + staffId));

        if (staffAccount.getRole() != Role.CS_STAFF) {
            throw new IllegalArgumentException("User is not a staff member");
        }

        List<StaffAssignment> assignments = staffAssignmentRepository
                .findActiveAssignmentsByStaffAccountId(staffId);

        return buildStaffResponse(staffAccount, assignments, null);
    }

    //Update staff station assignments
    @Transactional
    public StaffResponse updateStaffAssignments(Long staffId, List<Long> newStationIds) {
        log.info("Updating station assignments for staff: {}", staffId);

        Account staffAccount = accountRepository.findById(staffId)
                .orElseThrow(() -> new IllegalArgumentException("Staff not found with ID: " + staffId));

        if (staffAccount.getRole() != Role.CS_STAFF) {
            throw new IllegalArgumentException("User is not a staff member");
        }

        // Validate all stations exist
        List<ChargingStation> stations = stationRepository.findAllById(newStationIds);
        if (stations.size() != newStationIds.size()) {
            throw new IllegalArgumentException("One or more station IDs are invalid");
        }

        // Deactivate all existing assignments
        List<StaffAssignment> existingAssignments = staffAssignmentRepository
                .findActiveAssignmentsByStaffAccountId(staffId);

        existingAssignments.forEach(assignment -> assignment.setActive(false));
        staffAssignmentRepository.saveAll(existingAssignments);

        // Create new assignments
        OffsetDateTime now = OffsetDateTime.now();
        List<StaffAssignment> newAssignments = stations.stream()
                .map(station -> {
                    StaffAssignment assignment = new StaffAssignment();
                    assignment.setStaffAccountId(staffId);
                    assignment.setStation(station);
                    assignment.setAssignedAt(now);
                    assignment.setActive(true);
                    return assignment;
                })
                .collect(Collectors.toList());

        staffAssignmentRepository.saveAll(newAssignments);
        log.info("Updated station assignments for staff {}: {} stations", staffId, newAssignments.size());

        return buildStaffResponse(staffAccount, newAssignments, null);
    }

    //Deactivate staff user
    @Transactional
    public void deactivateStaff(Long staffId) {
        log.info("Deactivating staff: {}", staffId);

        Account staffAccount = accountRepository.findById(staffId)
                .orElseThrow(() -> new IllegalArgumentException("Staff not found with ID: " + staffId));

        if (staffAccount.getRole() != Role.CS_STAFF) {
            throw new IllegalArgumentException("User is not a staff member");
        }

        // Deactivate account
        staffAccount.setStatus(AccountStatus.INACTIVE);
        staffAccount.setUpdatedAt(OffsetDateTime.now());
        accountRepository.save(staffAccount);

        // Deactivate all assignments
        List<StaffAssignment> assignments = staffAssignmentRepository
                .findActiveAssignmentsByStaffAccountId(staffId);
        assignments.forEach(assignment -> assignment.setActive(false));
        staffAssignmentRepository.saveAll(assignments);

        log.info("Staff {} deactivated", staffId);
    }

    //HELPER METHODS

    private StaffResponse buildStaffResponse(Account account, List<StaffAssignment> assignments, String temporaryPassword) {
        List<StaffResponse.StaffStationInfo> stationInfos = assignments.stream()
                .map(assignment -> StaffResponse.StaffStationInfo.builder()
                        .stationId(assignment.getStation().getId())
                        .stationName(assignment.getStation().getName())
                        .stationLocation(assignment.getStation().getLocation())
                        .assignedAt(assignment.getAssignedAt())
                        .build())
                .collect(Collectors.toList());

        return StaffResponse.builder()
                .id(account.getId())
                .email(account.getEmail())
                .phone(account.getPhone())
                .fullName(account.getFullName())
                .status(account.getStatus())
                .createdAt(account.getCreatedAt())
                .assignedStations(stationInfos)
                .temporaryPassword(temporaryPassword) // Only set when creating
                .build();
    }

    private String generateSecurePassword() {
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);

        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int index = random.nextInt(PASSWORD_CHARS.length());
            password.append(PASSWORD_CHARS.charAt(index));
        }

        return password.toString();
    }
}
