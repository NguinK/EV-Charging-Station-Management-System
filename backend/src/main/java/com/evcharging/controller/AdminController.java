package com.evcharging.controller;

import com.evcharging.dto.LoginDTO;
import com.evcharging.dto.AdminResponseDTO;
import com.evcharging.service.AdminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO dto) {
        Optional<AdminResponseDTO> admin = adminService.login(dto);
        if (admin.isPresent()) {
            return ResponseEntity.ok(admin.get());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed");
        }
    }
}