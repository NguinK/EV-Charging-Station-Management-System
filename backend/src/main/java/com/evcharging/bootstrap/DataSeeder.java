package com.evcharging.bootstrap;

import com.evcharging.entity.Account;
import com.evcharging.entity.Admin;
import com.evcharging.enums.AccountStatus;
import com.evcharging.enums.AdminStatus;
import com.evcharging.enums.Role;
import com.evcharging.repository.AccountRepository;
import com.evcharging.repository.AdminRepository;
import com.evcharging.config.SuperAdminProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Data seeder responsible for initializing essential data on application startup.
 * This includes creating the super admin account if configured.
 */
@Slf4j
@Component
@Order(1)
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final AccountRepository accountRepository;
    private final AdminRepository adminRepository;
    private final SuperAdminProperties superAdminProperties;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Starting data seeding process...");

        initializeSuperAdmin();

        log.info("Data seeding completed successfully");
    }

    /**
     * Initialize the Super Admin account if enabled and not already existing.
     * This method is idempotent - it will not create duplicate accounts.
     */
    private void initializeSuperAdmin() {
        if (!superAdminProperties.isEnabled()) {
            log.info("Super Admin initialization is disabled");
            return;
        }

        String email = superAdminProperties.getEmail();
        String password = superAdminProperties.getPassword();
        String fullName = superAdminProperties.getFullName();

        // Validate configuration
        if (email == null || email.isBlank()) {
            log.warn("Super Admin email not configured. Skipping super admin initialization.");
            return;
        }

        if (password == null || password.isBlank()) {
            log.warn("Super Admin password not configured. Skipping super admin initialization.");
            return;
        }

        if (fullName == null || fullName.isBlank()) {
            fullName = "Super Administrator"; // Default value
        }

        // Check if super admin already exists
        if (accountRepository.existsByEmail(email)) {
            log.info("Super Admin account already exists with email: {}", email);
            return;
        }

        log.info("Creating Super Admin account with email: {}", email);

        try {
            // Create Account entity
            Account account = Account.builder()
                    .email(email)
                    .fullName(fullName)
                    .password(passwordEncoder.encode(password))
                    .role(Role.SUPER_ADMIN)
                    .status(AccountStatus.ACTIVE)
                    .build();

            account = accountRepository.save(account);

            // Create Admin entity linked to the account
            Admin admin = new Admin();
            admin.setFullName(fullName);
            admin.setAccount(account);
            admin.setStatus(AdminStatus.ACTIVE);

            adminRepository.save(admin);

            log.info("✅ Super Admin account created successfully: {}", email);
        } catch (Exception e) {
            log.error("❌ Failed to create Super Admin account", e);
            throw new RuntimeException("Failed to initialize Super Admin account", e);
        }
    }
}

