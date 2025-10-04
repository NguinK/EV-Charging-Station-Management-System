package com.evcharging.service;

import com.evcharging.config.jwtUtil;
import com.evcharging.dto.*;
import com.evcharging.entity.Admin;
import com.evcharging.repository.AdminRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;


import java.util.Optional;

@Service
@RequiredArgsConstructor
@Validated
public class AdminService {
    private final AdminRepository adminRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final jwtUtil jwtUtil;

    @Transactional(readOnly = true)
    public Optional<LoginResponseDTO> login(LoginDTO dto) {
        return adminRepository.findByAccount_Email(dto.getEmail())
                .filter(a -> passwordEncoder.matches(dto.getPassword(), a.getAccount().getPassword()))
                .map(a -> {
                    String token = jwtUtil.generateToken(a.getAccount().getEmail());
                    LoginResponseDTO response = new LoginResponseDTO();
                    response.setToken(token);
                    response.setFullName(a.getFullName());
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
        Admin admin = modelMapper.map(dto, Admin.class);
        Admin saved = adminRepository.save(admin);
        return modelMapper.map(saved, AdminResponseDTO.class);
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
