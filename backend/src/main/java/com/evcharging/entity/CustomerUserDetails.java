package com.evcharging.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Data
@AllArgsConstructor
public class CustomerUserDetails implements UserDetails {

    private Long id; // accountId hoặc driverId
    private String username;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    //  Các method này bắt buộc phải override
    @Override
    public boolean isAccountNonExpired() {
        return true; // hoặc logic kiểm tra DB
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // hoặc logic kiểm tra DB
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // hoặc logic kiểm tra DB
    }

    @Override
    public boolean isEnabled() {
        return true; // hoặc logic kiểm tra DB
    }
}