package com.evcharging.service;

import com.evcharging.dto.DriverProfileDTO;
import com.evcharging.entity.EVDriver;
import com.evcharging.repository.EVDriverRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminDriverService {

    private final EVDriverRepository driverRepository;

    public AdminDriverService(EVDriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    public DriverProfileDTO createDriver(DriverProfileDTO dto) {
        EVDriver driver = new EVDriver();
        driver.setFullName(dto.getFullName());
        driver.setDateOfBirth(dto.getDateOfBirth());
        driver.setAddress(dto.getAddress());
        driver.setDriverLicense(dto.getDriverLicense());
        driver.setVehicleNumber(dto.getVehicleNumber());
        driver.setVehicleType(dto.getVehicleType());

        EVDriver saved = driverRepository.save(driver);
        return mapToProfileDTO(saved);
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
        return dto;
    }

    public List<DriverProfileDTO> getAllDrivers() {
        return driverRepository.findAll()
                .stream()
                .map(this::mapToProfileDTO)
                .collect(Collectors.toList());
    }


}