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
import com.evcharging.repository.EVDriverRepository;
import com.evcharging.repository.CS_StaffRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class AuthService {

    private final AccountRepository accountRepository;
    private final EVDriverRepository evDriverRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AdminRepository adminRepository;
    private final CS_StaffRepository CSStaffRepository;

    public AuthService(AccountRepository accountRepository,
                       EVDriverRepository evDriverRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil,
                       AdminRepository adminRepository,
                       CS_StaffRepository CSStaffRepository) {
        this.accountRepository = accountRepository;
        this.evDriverRepository = evDriverRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.adminRepository = adminRepository;
        this.CSStaffRepository = CSStaffRepository;
    }

    @Transactional
    public EVDriverResponseDTO registerDriver(EVDriverCreateDTO dto) {
        // 1. Kiểm tra email đã tồn tại chưa
        if (accountRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email đã được sử dụng");
        }

        // 2. Tạo account
        Account account = new Account();
        account.setEmail(dto.getEmail());
        account.setPhone(dto.getPhone());
        account.setPassword(passwordEncoder.encode(dto.getPassword()));
        account.setRole(Role.EV_DRIVER);
        account.setEnabled(true);
        accountRepository.save(account);

        // 3. Tạo profile EVDriver
        EVDriver driver = new EVDriver();
        driver.setAccount(account);
        driver.setFullName(dto.getFullName());
        driver.setDriverLicense(dto.getDriverLicense());
        driver.setVehicleNumber(dto.getVehicleNumber());
        driver.setVehicleType(dto.getVehicleType());
        driver.setAddress(dto.getAddress());
        driver.setDateOfBirth(dto.getDateOfBirth());
        evDriverRepository.save(driver);

        // 4. Trả về DTO
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

        // 4. Lấy fullName
        String fullName = switch (account.getRole()) {
            case EV_DRIVER -> evDriverRepository.findByAccountId(account.getId())
                    .map(EVDriver::getFullName)
                    .orElse(null);
            case ADMIN -> adminRepository.findByAccountId(account.getId())
                    .map(Admin::getFullName)
                    .orElse(null);
            case CS_STAFF -> CSStaffRepository.findByAccountId(account.getId())
                    .map(CS_Staff::getFullName)
                    .orElse(null);
            default -> null;
        };

        return new LoginResponseDTO(token, fullName, account.getEmail(), account.getRole(), expiresAt);
    }
}