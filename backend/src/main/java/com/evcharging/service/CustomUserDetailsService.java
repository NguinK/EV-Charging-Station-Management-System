package com.evcharging.service;

import com.evcharging.entity.Admin;
import com.evcharging.repository.AdminRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
    public class CustomUserDetailsService implements UserDetailsService {

        private final AdminRepository adminRepository;

        public CustomUserDetailsService(AdminRepository adminRepository) {
            this.adminRepository = adminRepository;
        }

        @Override
        public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
            Admin admin = adminRepository.findByAccount_Email(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Admin not found"));

            return new org.springframework.security.core.userdetails.User(
                    admin.getAccount().getEmail(),
                    admin.getAccount().getPassword(),
                    List.of(new SimpleGrantedAuthority(admin.getAccount().getRole().name()))
            );
        }

    }


