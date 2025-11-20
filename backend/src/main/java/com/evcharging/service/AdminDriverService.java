package com.evcharging.service;

import com.evcharging.dto.DriverProfileDTO;
import com.evcharging.entity.Account;
import com.evcharging.entity.EVDriver;
import com.evcharging.enums.Role;
import com.evcharging.repository.AccountRepository;
import com.evcharging.repository.EVDriverRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminDriverService {

    private final EVDriverRepository driverRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminDriverService(EVDriverRepository driverRepository,
                              AccountRepository accountRepository,
                              PasswordEncoder passwordEncoder) {
        this.driverRepository = driverRepository;
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public DriverProfileDTO createDriver(DriverProfileDTO dto) {
        // Tạo Account mới
        Account account = new Account();
        account.setEmail(dto.getEmail());
        account.setPhone(dto.getPhone());
        account.setPassword(passwordEncoder.encode(dto.getPassword()));
        account.setRole(Role.EV_DRIVER); // gán role mặc định
        Account savedAccount = accountRepository.save(account);

        // Tạo EVDriver gắn với Account
        EVDriver driver = new EVDriver();
        driver.setAccount(savedAccount);
        driver.setFullName(dto.getFullName());
        driver.setDateOfBirth(dto.getDateOfBirth());
        driver.setAddress(dto.getAddress());
        driver.setDriverLicense(dto.getDriverLicense());
        driver.setVehicleNumber(dto.getVehicleNumber());
        driver.setVehicleType(dto.getVehicleType());

        EVDriver savedDriver = driverRepository.save(driver);

        // B3: Trả về DTO
        return mapToProfileDTO(savedDriver);
    }


    public DriverProfileDTO updateDriver(Long driverId, DriverProfileDTO dto) {
        EVDriver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + driverId));

        driver.setFullName(dto.getFullName());
        driver.setDateOfBirth(dto.getDateOfBirth());
        driver.setAddress(dto.getAddress());
        driver.setDriverLicense(dto.getDriverLicense());
        driver.setVehicleNumber(dto.getVehicleNumber());
        driver.setVehicleType(dto.getVehicleType());

        EVDriver saved = driverRepository.save(driver);
        return mapToProfileDTO(saved);
    }

    public void deleteDriver(Long driverId) {
        if (!driverRepository.existsById(driverId)) {
            throw new RuntimeException("Driver not found with id: " + driverId);
        }
        driverRepository.deleteById(driverId);
    }

    public DriverProfileDTO getDriver(Long driverId) {
        EVDriver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + driverId));
        return mapToProfileDTO(driver);
    }

    private DriverProfileDTO mapToProfileDTO(EVDriver driver) {
        DriverProfileDTO dto = new DriverProfileDTO();
        dto.setFullName(driver.getFullName());
        dto.setDateOfBirth(driver.getDateOfBirth());
        dto.setAddress(driver.getAddress());
        dto.setDriverLicense(driver.getDriverLicense());
        dto.setVehicleNumber(driver.getVehicleNumber());
        dto.setVehicleType(driver.getVehicleType());
        dto.setEmail(driver.getAccount().getEmail());
        dto.setPhone(driver.getAccount().getPhone());

        return dto;
    }

    public List<DriverProfileDTO> getAllDrivers() {
        return driverRepository.findAll()
                .stream()
                .map(this::mapToProfileDTO)
                .collect(Collectors.toList());
    }


}