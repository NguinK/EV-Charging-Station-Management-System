package com.evcharging.controller;

import com.evcharging.dto.ReservationCreateDTO;
import com.evcharging.dto.ReservationResponseDTO;
import com.evcharging.entity.Account;
import com.evcharging.service.ReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/drivers/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    // Tạo mới một reservation (driver đặt chỗ)
    @PostMapping("/bookReservation")
    public ResponseEntity<ReservationResponseDTO> createReservation(
            @RequestBody ReservationCreateDTO dto,
            @AuthenticationPrincipal Account user) {

        ReservationResponseDTO response = reservationService.createReservation(user.getId(), dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Lấy thông tin chi tiết một reservation
    @GetMapping("/getDetails/{id}")
    public ResponseEntity<ReservationResponseDTO> getReservation(@PathVariable Long id) {
        ReservationResponseDTO response = reservationService.getReservation(id);
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
    public ResponseEntity<List<ReservationResponseDTO>> getDriverReservations(
            @AuthenticationPrincipal Account user) {
        List<ReservationResponseDTO> reservations = reservationService.getReservationsByDriver(user.getId());
        return ResponseEntity.ok(reservations);
    }
}