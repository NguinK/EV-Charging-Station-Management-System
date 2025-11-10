package com.evcharging.service;

import com.evcharging.dto.DriverProfileDTO;
import com.evcharging.dto.TransactionDTO;
import com.evcharging.entity.EVDriver;
import com.evcharging.entity.Transaction;
import com.evcharging.repository.EVDriverRepository;
import com.evcharging.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EVDriverService {

    private final EVDriverRepository driverRepository;
    private final TransactionRepository transactionRepository;

    // Lấy hồ sơ tài xế
    public DriverProfileDTO getProfile(Long driverId) {
        EVDriver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + driverId));
        return mapToProfileDTO(driver);
    }

    // Lấy lịch sử giao dịch
    public List<TransactionDTO> getTransactions(Long driverId, LocalDate from, LocalDate to) {
        ZoneOffset vnOffset = ZoneOffset.ofHours(7);

        OffsetDateTime start = from
                .atStartOfDay()
                .atOffset(vnOffset);

        OffsetDateTime end = to
                .plusDays(1)
                .atStartOfDay()
                .minusNanos(1)
                .atOffset(vnOffset);

        return transactionRepository
                .findByDriverIdAndTimestampBetween(
                        driverId,
                        start,
                        end
                )
                .stream()
                .map(this::mapToTransactionDTO)
                .collect(Collectors.toList());
    }

    //Mapper methods
    private DriverProfileDTO mapToProfileDTO(EVDriver driver) {
        DriverProfileDTO dto = new DriverProfileDTO();
        dto.setFullName(driver.getFullName());
        dto.setDateOfBirth(driver.getDateOfBirth());
        dto.setAddress(driver.getAddress());
        dto.setDriverLicense(driver.getDriverLicense());
        dto.setVehicleNumber(driver.getVehicleNumber());
        dto.setVehicleType(driver.getVehicleType());

        if (driver.getAccount() != null) {
            dto.setPhone(driver.getAccount().getPhone());
            dto.setEmail(driver.getAccount().getEmail());
        }
        return dto;
    }

    private TransactionDTO mapToTransactionDTO(Transaction tx) {
        TransactionDTO dto = new TransactionDTO();
        dto.setId(tx.getId());

        if (tx.getDriver() != null) {
            dto.setDriverId(tx.getDriver().getId());
            dto.setDriverName(tx.getDriver().getFullName());
        }

        if (tx.getSession() != null) {
            dto.setSessionId(tx.getSession().getId());
        }

        dto.setAmount(tx.getAmount().doubleValue());
        dto.setCurrency(tx.getCurrency());
        dto.setPaymentMethod(tx.getPaymentMethod().name());
        dto.setPaymentType(tx.getType().name()); // map enum type vào paymentType
        dto.setTransactionTime(tx.getTimestamp());
        dto.setStatus(tx.getStatus().name());
        dto.setInvoiceNumber(tx.getInvoiceNumber());
        dto.setPaidAt(tx.getPaidAt());
        dto.setDescription("Invoice: " + tx.getInvoiceNumber());

        return dto;
    }
}