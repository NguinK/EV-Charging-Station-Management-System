package com.evcharging.service;

import com.evcharging.config.JwtUtil;
import com.evcharging.dto.EVDriverCreateDTO;
import com.evcharging.dto.EVDriverResponseDTO;
import com.evcharging.dto.LoginDTO;
import com.evcharging.dto.LoginResponseDTO;
import com.evcharging.entity.Account;
import com.evcharging.entity.Admin;
import com.evcharging.entity.CS_Staff;
import com.evcharging.entity.EVDriver;
import com.evcharging.enums.Role;
import com.evcharging.repository.AccountRepository;
import com.evcharging.repository.AdminRepository;
import com.evcharging.repository.CS_StaffRepository;
import com.evcharging.repository.EVDriverRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;

@Slf4j
@Service
public class AuthService {

    private final AccountRepository accountRepository;
    private final EVDriverRepository evDriverRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AdminRepository adminRepository;
    private final CS_StaffRepository CSStaffRepository;
    private final WalletService walletService;

    public AuthService(AccountRepository accountRepository,
                       EVDriverRepository evDriverRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil,
                       AdminRepository adminRepository,
                       CS_StaffRepository CSStaffRepository,
                       WalletService walletService) {
        this.accountRepository = accountRepository;
        this.evDriverRepository = evDriverRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.adminRepository = adminRepository;
        this.CSStaffRepository = CSStaffRepository;
        this.walletService = walletService;
    }

    @Transactional
    public EVDriverResponseDTO registerDriver(EVDriverCreateDTO dto) {
        // 1. Kiểm tra email đã tồn tại chưa
        if (accountRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email đã được sử dụng");
        }
        if (accountRepository.findByPhone(dto.getPhone()).isPresent()) {
            throw new IllegalArgumentException("Số điện thoại đã được sử dụng");
        }
        LocalDate dob = dto.getDateOfBirth();
        if (dob.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Ngày sinh không hợp lệ (không thể ở tương lai)");
        }
        int age = Period.between(dob, LocalDate.now()).getYears();
        if (age < 18) {
            throw new IllegalArgumentException("Tài xế phải ít nhất 18 tuổi");
        }

        // 2. Tạo account
        Account account = new Account();
        account.setEmail(dto.getEmail());
        account.setPhone(dto.getPhone());
        account.setPassword(passwordEncoder.encode(dto.getPassword()));
        account.setRole(Role.EV_DRIVER);
        account.setEnabled(true);
        account.setFullName(dto.getFullName());
        accountRepository.save(account);


        // 3. Tạo profile EVDriver
        EVDriver driver = new EVDriver();
        driver.setAccount(account);
        driver.setFullName(dto.getFullName());
        driver.setPhone(dto.getPhone());
        driver.setDriverLicense(dto.getDriverLicense());
        driver.setVehicleNumber(dto.getVehicleNumber());
        driver.setVehicleType(dto.getVehicleType());
        driver.setBatteryCapacity(dto.getBatteryCapacity());
        driver.setConnectorType(dto.getConnectorType());
        driver.setAddress(dto.getAddress());
        driver.setDateOfBirth(dto.getDateOfBirth());
        evDriverRepository.save(driver);

        // 4. Auto create wallet
        try {
            walletService.createWallet(account.getId());
            log.info(" Wallet auto-created for new driver: {} (accountId: {})",
                    account.getEmail(), account.getId());
        } catch (Exception e) {
            log.error("Failed to create wallet for new driver: {} - Error: {}",
                    account.getEmail(), e.getMessage(), e);
        }

        // 5. Trả về DTO
        return new EVDriverResponseDTO(driver.getId(), driver.getFullName(),
                account.getEmail(), driver.getVehicleNumber());
    }

    public LoginResponseDTO login(LoginDTO dto) {
        // 1. Tìm account theo email
        Account account = accountRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Email không tồn tại"));

        // 2. Kiểm tra password
        if (!passwordEncoder.matches(dto.getPassword(), account.getPassword())) {
            throw new IllegalArgumentException("Sai mật khẩu");
        }

        // 3. Sinh JWT token
        String token = jwtUtil.generateToken(account.getEmail(), account.getRole());
        Instant expiresAt = jwtUtil.getExpirationFromToken(token);

        String fullName = null;
        Long driverId = null;
        Long adminId = null;
        Long staffId = null;

        switch (account.getRole()) {
            case EV_DRIVER -> {
                EVDriver driver = evDriverRepository.findByAccountId(account.getId())
                        .orElse(null);
                if (driver != null) {
                    fullName = driver.getFullName();
                    driverId = driver.getId();
                }
            }
            case ADMIN -> {
                Admin admin = adminRepository.findByAccountId(account.getId())
                        .orElse(null);
                if (admin != null) {
                    fullName = admin.getFullName();
                    adminId = admin.getId(); // lấy id admin
                }
            }
            case CS_STAFF -> {
                CS_Staff staff = CSStaffRepository.findByAccountId(account.getId())
                        .orElse(null);
                if (staff != null) {
                    fullName = staff.getFullName();
                    staffId = staff.getId(); // lấy id staff
                }
            }
            default -> fullName = null;
        }

        return new LoginResponseDTO(token, fullName, account.getEmail(),
                account.getRole(), expiresAt, driverId, adminId, staffId);
    }
}