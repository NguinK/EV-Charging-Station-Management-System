package com.evcharging.service;

import com.evcharging.config.JwtUtil;
import com.evcharging.dto.*;
import com.evcharging.entity.Account;
import com.evcharging.entity.Admin;
import com.evcharging.repository.AdminRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.modelmapper.ModelMapper;
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
    private final JwtUtil jwtUtil;

    @Transactional(readOnly = true)
    public Optional<LoginResponseDTO> login(LoginDTO dto) {
        return adminRepository.findByAccount_Email(dto.getEmail())
                .filter(a -> passwordEncoder.matches(dto.getPassword(), a.getAccount().getPassword()))
                .map(a -> {
                    String token = jwtUtil.generateToken(a.getAccount().getEmail(), a.getAccount().getRole());
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
        return adminRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public Optional<AdminResponseDTO> getAdminById(Long id) {
        return adminRepository.findById(id).map(this::mapToResponse);
    }

    @Transactional
    public AdminResponseDTO createAdmin(@Valid AdminCreateDTO dto) {
        // Tạo Account từ DTO
        Account account = new Account();
        account.setEmail(dto.getEmail());
        account.setPassword(passwordEncoder.encode(dto.getPassword()));
        account.setRole(dto.getRole());
        account.setEnabled(dto.isActive());
        account.setPhone(dto.getPhone());
        account.setAccountNonExpired(true);
        account.setAccountNonLocked(true);

        // Tạo Admin và gắn Account
        Admin admin = new Admin();
        admin.setFullName(dto.getFullName());
        admin.setAccount(account);

        // Lưu vào DB
        Admin saved = adminRepository.save(admin);

        // Trả về DTO đã map
        return mapToResponse(saved);
    }

        // Hàm map sang DTO trả về
        private AdminResponseDTO mapToResponse(Admin admin){
            AdminResponseDTO response = new AdminResponseDTO();
            response.setId(admin.getId());
            response.setFullName(admin.getFullName());

            Account acc = admin.getAccount();
            response.setEmail(acc.getEmail());
            response.setPhone(acc.getPhone());
            response.setRole(acc.getRole());
            response.setActive(acc.isEnabled());

            if (admin.getCreatedAt() != null) {
                response.setCreatedAt(admin.getCreatedAt());
            }
            if (admin.getUpdatedAt() != null) {
                response.setUpdatedAt(admin.getUpdatedAt());
            }
            return response;
        }


    @Transactional
    public Optional<AdminResponseDTO> updateAdmin(Long id, @Valid AdminUpdateDTO dto) {
        return adminRepository.findById(id).map(entity -> {
            entity.setFullName(dto.getFullName());
            // Lấy Account để cập nhật
           Account account = entity.getAccount();

            // Cập nhật số điện thoại nếu có
            if (dto.getPhone() != null && !dto.getPhone().isBlank()) {

                account.setPhone(dto.getPhone());
            }

            // Cập nhật password nếu có (chỉ khi người dùng muốn đổi mật khẩu)
            if (dto.getNewPassword() != null && !dto.getNewPassword().isBlank()) {
                account.setPassword(passwordEncoder.encode(dto.getNewPassword()));
            }

            // Cập nhật trạng thái active (cho phép SuperAdmin khóa/mở tài khoản)
            account.setEnabled(dto.isActive());


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
