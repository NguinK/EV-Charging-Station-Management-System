package com.evcharging.controller;

import com.evcharging.dto.ReservationCreateDTO;
import com.evcharging.dto.ReservationResponseDTO;
import com.evcharging.entity.Account;
import com.evcharging.entity.EVDriver;
import com.evcharging.repository.AccountRepository;
import com.evcharging.repository.EVDriverRepository;
import com.evcharging.service.ReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/drivers/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final EVDriverRepository driverRepository;
    private final AccountRepository accountRepository;


    public ReservationController(ReservationService reservationService, EVDriverRepository driverRepository,  AccountRepository accountRepository) {
        this.reservationService = reservationService;
        this.driverRepository = driverRepository;
        this.accountRepository = accountRepository;
    }

    // Tạo mới một reservation (driver đặt chỗ)
//    @PostMapping("/bookReservation")
//    public ResponseEntity<ReservationResponseDTO> createReservation(
//            @RequestBody ReservationCreateDTO dto,
//            @AuthenticationPrincipal Account user) {
//
//        // Lấy EVDriver từ accountId
//        EVDriver driver = driverRepository.findByAccountId(user.getId())
//                .orElseThrow(() -> new RuntimeException("Driver not found"));
//
//        // Gọi service với driverId
//        ReservationResponseDTO response = reservationService.createReservation(driver.getId(), dto);
//        return ResponseEntity.status(HttpStatus.CREATED).body(response);
//    }

    // Lấy thông tin chi tiết một reservation
    @GetMapping("/getDetails/{id}")
    public ResponseEntity<ReservationResponseDTO> getReservationDetails(@PathVariable Long id) {
        ReservationResponseDTO response = reservationService.getReservationDetails(id);
        return ResponseEntity.ok(response);
    }

    // Hủy một reservation
    @DeleteMapping("/deleteReservation/{id}")
    public ResponseEntity<Void> cancelReservation(@PathVariable Long id) {
        reservationService.cancelReservation(id);
        return ResponseEntity.noContent().build();
    }

    // Lấy danh sách reservation của driver hiện tại
    @GetMapping("/getList")
    public ResponseEntity<List<ReservationResponseDTO>> getDriverReservations() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<ReservationResponseDTO> reservations = reservationService.getReservationList(account.getId());
        return ResponseEntity.ok(reservations);
    }

    @PostMapping("/auto")
    public ResponseEntity<ReservationResponseDTO> autoApproveReservation(
            @AuthenticationPrincipal Account user,
            @RequestBody ReservationCreateDTO dto
    ) {
        // Lấy EVDriver từ accountId
        EVDriver driver = driverRepository.findByAccountId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver not found"));
        ReservationResponseDTO response = reservationService.createReservationAutoApprove(driver.getId(), dto);
        return ResponseEntity.ok(response);
    }
}