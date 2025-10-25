package com.evcharging.entity;

import com.evcharging.enums.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Entity
@Table(
        name = "account",
        indexes = {
                @Index(name = "idx_account_email", columnList = "email", unique = true),
                @Index(name = "idx_account_phone", columnList = "phone", unique = true)
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email
    @NotBlank
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    // Cho phép số 0xxxxxxxxx hoặc +84xxxxxxxxx
    @Pattern(regexp = "^(\\+84|0)\\d{9,10}$", message = "Phone number should be valid")
    @Column(nullable = false, length = 15, unique = true)
    private String phone;

    @NotBlank
    @Size(min = 6)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Role role;

    @Builder.Default
    private boolean accountNonExpired = true;
    @Builder.Default
    private boolean accountNonLocked = true;
    @Builder.Default
    private boolean credentialsNonExpired = true;
    @Builder.Default
    private boolean enabled = true;

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL)
    private EVDriver driver;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return (role != null) ? role.getGrantedAuthorities() : Collections.emptyList();
    }

    @Override public String getUsername() { return email; }
    @Override public boolean isAccountNonExpired() { return accountNonExpired; }
    @Override public boolean isAccountNonLocked() { return accountNonLocked; }
    @Override public boolean isCredentialsNonExpired() { return credentialsNonExpired; }
    @Override public boolean isEnabled() { return enabled; }

    @PrePersist
    @PreUpdate
    private void normalize() {
        if (email != null) email = email.trim().toLowerCase();
        if (phone != null) phone = phone.trim();
    }
}