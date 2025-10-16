package com.evcharging.controller;

import com.evcharging.dto.DriverProfileDTO;
import com.evcharging.dto.TransactionDTO;
import com.evcharging.service.EVDriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/drivers")
@RequiredArgsConstructor
public class EVDriverController {

    private final EVDriverService driverService;

    // Lấy hồ sơ tài xế
    @GetMapping("/getProfile/{driverId}")
    public ResponseEntity<DriverProfileDTO> getProfile(@PathVariable Long driverId) {
        return ResponseEntity.ok(driverService.getProfile(driverId));
    }

    // Lấy lịch sử giao dịch trong khoảng thời gian
    @GetMapping("/transactions/{driverId}")
    public ResponseEntity<List<TransactionDTO>> getTransactions(
            @PathVariable Long driverId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return ResponseEntity.ok(driverService.getTransactions(driverId, from, to));
    }
}
