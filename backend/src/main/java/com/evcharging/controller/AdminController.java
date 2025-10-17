package com.evcharging.controller;

import com.evcharging.dto.*;
import com.evcharging.service.AdminService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import jakarta.validation.Valid;
import java.util.Optional;

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
        Optional<LoginResponseDTO> admin = adminService.login(dto);
        if (admin.isPresent()) {
            return ResponseEntity.ok(admin.get());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed");
        }
    }
    // GET ALL ADMINS (có phân trang)
    @GetMapping("/auth/getAllAdmins")
    public Page<AdminResponseDTO> getAllAdmins(@ParameterObject Pageable pageable) {
        return adminService.getAllAdmins(pageable);
    }

    // GET ADMIN BY ID
    @GetMapping("/auth/ById/{id}")
    public ResponseEntity<AdminResponseDTO> getAdminById(@PathVariable Long id) {
        return adminService.getAdminById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // CREATE ADMIN
    @PostMapping("/auth/createAdmin")
    public ResponseEntity<AdminResponseDTO> createAdmin(@Valid @RequestBody AdminCreateDTO dto) {
        AdminResponseDTO created = adminService.createAdmin(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    //  UPDATE ADMIN
    @PutMapping("/auth/updateAdmin/{id}")
    public ResponseEntity<AdminResponseDTO> updateAdmin(@PathVariable Long id,
                                                        @Valid @RequestBody AdminUpdateDTO dto) {
        return adminService.updateAdmin(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE ADMIN
    @DeleteMapping("/auth/deleteAdmin/{id}")
    public ResponseEntity<Void> deleteAdmin(@PathVariable Long id) {
        return adminService.deleteAdmin(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }



}