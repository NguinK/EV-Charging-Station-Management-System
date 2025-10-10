package com.evcharging.controller;

import com.evcharging.dto.EVDriverCreateDTO;
import com.evcharging.dto.EVDriverResponseDTO;
import com.evcharging.dto.LoginDTO;
import com.evcharging.dto.LoginResponseDTO;
import com.evcharging.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // Đăng ký EV Driver
    @PostMapping("/register-driver")
    public ResponseEntity<EVDriverResponseDTO> registerDriver(@Valid @RequestBody EVDriverCreateDTO dto) {
        EVDriverResponseDTO response = authService.registerDriver(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Đăng nhập (dùng chung cho Driver, Staff, Admin)
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginDTO dto) {
        LoginResponseDTO response = authService.login(dto);
        return ResponseEntity.ok(response);
    }
}