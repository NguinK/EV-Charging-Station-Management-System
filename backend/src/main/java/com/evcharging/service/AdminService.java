package com.evcharging.service;

import com.evcharging.config.JwtUtil;
import com.evcharging.dto.*;
import com.evcharging.entity.Account;
import com.evcharging.entity.Admin;
import com.evcharging.repository.AdminRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;


import java.time.ZoneId;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Validated
public class AdminService {
    private final AdminRepository adminRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional(readOnly = true)
    public Optional<LoginResponseDTO> login(LoginDTO dto) {
        return adminRepository.findByAccount_Email(dto.getEmail())
                .filter(a -> passwordEncoder.matches(dto.getPassword(), a.getAccount().getPassword()))
                .map(a -> {
                    String token = jwtUtil.generateToken(a.getAccount().getEmail());
                    LoginResponseDTO response = new LoginResponseDTO();
                    response.setToken(token);
                    response.setFullName(a.getFullName());
                    response.setEmail(a.getAccount().getEmail());
                    response.setRole(a.getAccount().getRole());
                    return response;

                });
    }



    @Transactional(readOnly = true)
    public Page<AdminResponseDTO> getAllAdmins(Pageable pageable) {
        return adminRepository.findAll(pageable).map(a -> modelMapper.map(a, AdminResponseDTO.class));
    }

    @Transactional(readOnly = true)
    public Optional<AdminResponseDTO> getAdminById(Long id) {
        return adminRepository.findById(id).map(a -> modelMapper.map(a, AdminResponseDTO.class));
    }

    @Transactional
    public AdminResponseDTO createAdmin(@Valid AdminCreateDTO dto) {
        // Tạo Account từ DTO
        Account account = new Account();
        account.setEmail(dto.getEmail());
        account.setPassword(passwordEncoder.encode(dto.getPassword()));
        account.setRole(dto.getRole());
        account.setEnabled(dto.isActive());
        account.setFullName(dto.getFullName());
        account.setPhone(dto.getPhone());
        account.setAccountNonExpired(true);
        account.setAccountNonLocked(true);

        // Map sang Admin
        Admin admin = new Admin();
        admin.setFullName(dto.getFullName());
        admin.setAccount(account); // gán account cho admin
        Admin saved = adminRepository.save(admin);

        // Map sang DTO trả về
        AdminResponseDTO response = new AdminResponseDTO();
        response.setId(saved.getId());
        response.setFullName(saved.getFullName());
        response.setEmail(saved.getAccount().getEmail());
        response.setPhone(saved.getAccount().getPhone());
        response.setRole(saved.getAccount().getRole());
        response.setActive(saved.getAccount().isEnabled());

        if (saved.getCreatedAt() != null) {
            response.setCreatedAt(saved.getCreatedAt()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime());
        }
        if (saved.getUpdatedAt() != null) {
            response.setUpdatedAt(saved.getUpdatedAt()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime());
        }
        return response;
    }


    @Transactional
    public Optional<AdminResponseDTO> updateAdmin(Long id, @Valid AdminUpdateDTO dto) {
        return adminRepository.findById(id).map(entity -> {
            entity.setFullName(dto.getFullName());
            Admin saved = adminRepository.save(entity);
            return modelMapper.map(saved, AdminResponseDTO.class);
        });
    }

    @Transactional
    public boolean deleteAdmin(Long id) {
        return adminRepository.findById(id).map(a -> {
            adminRepository.delete(a);
            return true;
        }).orElse(false);
    }

}
