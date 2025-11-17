package com.evcharging.service.admin;

import com.evcharging.dto.admin.*;
import com.evcharging.entity.Account;
import com.evcharging.enums.*;
import com.evcharging.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

//Service for admin user management
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final AccountRepository accountRepo;

    //Lấy danh sách tất cả người dùng (phân trang)
    public Page<UserResponse> getAllUsers(int page, int size, Role role) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<Account> accounts;
        if (role != null) {
            accounts = accountRepo.findByRole(role, pageable);
        } else {
            accounts = accountRepo.findAll(pageable);
        }

        return accounts.map(this::mapToUserResponse);
    }

    //Lấy thống kê người dùng
    public UserStatisticsResponse getUserStatistics() {
        log.info("Getting user statistics");

        List<Account> allAccounts = accountRepo.findAll();

        long totalUsers = allAccounts.size();
        long activeUsers = allAccounts.stream()
                .filter(a -> a.getStatus() == AccountStatus.ACTIVE)
                .count();
        long inactiveUsers = allAccounts.stream()
                .filter(a -> a.getStatus() == AccountStatus.INACTIVE)
                .count();
        long suspendedUsers = allAccounts.stream()
                .filter(a -> a.getStatus() == AccountStatus.SUSPENDED)
                .count();

        // Đếm theo role
        long drivers = allAccounts.stream()
                .filter(a -> a.getRole() == Role.EV_DRIVER)
                .count();
        long staff = allAccounts.stream()
                .filter(a -> a.getRole() == Role.CS_STAFF)
                .count();
        long admins = allAccounts.stream()
                .filter(a -> a.getRole() == Role.ADMIN)
                .count();

        // User mới trong 30 ngày
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        long newUsersLast30Days = allAccounts.stream()
                .filter(a -> a.getCreatedAt().isAfter(thirtyDaysAgo))
                .count();

        return UserStatisticsResponse.builder()
                .totalUsers(totalUsers)
                .activeUsers(activeUsers)
                .inactiveUsers(inactiveUsers)
                .suspendedUsers(suspendedUsers)
                .totalDrivers(drivers)
                .totalStaff(staff)
                .totalAdmins(admins)
                .newUsersLast30Days(newUsersLast30Days)
                .build();
    }

    //Lấy chi tiết người dùng
    public UserDetailResponse getUserDetail(Long userId) {
        Account account = accountRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // TODO: Lấy thêm thông tin về sessions, payments, subscriptions

        return UserDetailResponse.builder()
                .id(account.getId())
                .email(account.getEmail())
                .phone(account.getPhone())
                .fullName(account.getFullName())
                .role(account.getRole())
                .status(account.getStatus())
                .accountNonExpired(account.getAccountNonExpired())
                .accountNonLocked(account.getAccountNonLocked())
                .credentialsNonExpired(account.getCredentialsNonExpired())
                .enabled(account.getEnabled())
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .build();
    }

    //Cập nhật role của người dùng
    @Transactional
    public UserResponse updateUserRole(Long userId, Role newRole) {
        log.info("Updating user {} role to {}", userId, newRole);

        Account account = accountRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Role oldRole = account.getRole();
        account.setRole(newRole);
        account.setUpdatedAt(LocalDateTime.now());

        account = accountRepo.save(account);

        log.info("User {} role changed: {} → {}", userId, oldRole, newRole);

        return mapToUserResponse(account);
    }

    //Cập nhật status của người dùng
    @Transactional
    public UserResponse updateUserStatus(Long userId, AccountStatus newStatus) {
        log.info("Updating user {} status to {}", userId, newStatus);

        Account account = accountRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        AccountStatus oldStatus = account.getStatus();
        account.setStatus(newStatus);
        account.setUpdatedAt(LocalDateTime.now());

        // Nếu suspend, lock account
        if (newStatus == AccountStatus.SUSPENDED) {
            account.setAccountNonLocked(false);
        } else if (newStatus == AccountStatus.ACTIVE) {
            account.setAccountNonLocked(true);
        }

        account = accountRepo.save(account);

        log.info("User {} status changed: {} → {}", userId, oldStatus, newStatus);

        return mapToUserResponse(account);
    }

    //Tìm kiếm người dùng
    public List<UserResponse> searchUsers(String keyword) {
        log.info("Searching users: {}", keyword);

        List<Account> accounts = accountRepo.searchByKeyword(keyword);

        return accounts.stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    //Xóa người dùng (soft delete)
    @Transactional
    public void deleteUser(Long userId) {
        log.warn("Deleting user: {}", userId);

        Account account = accountRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Soft delete: set status = INACTIVE
        account.setStatus(AccountStatus.INACTIVE);
        account.setEnabled(false);
        account.setUpdatedAt(LocalDateTime.now());

        accountRepo.save(account);

        log.info("User {} deleted (soft delete)", userId);
    }

    // ========== HELPER METHODS ==========

    private UserResponse mapToUserResponse(Account account) {
        return UserResponse.builder()
                .id(account.getId())
                .email(account.getEmail())
                .phone(account.getPhone())
                .fullName(account.getFullName())
                .role(account.getRole())
                .status(account.getStatus())
                .enabled(account.getEnabled())
                .createdAt(account.getCreatedAt())
                .build();
    }
}