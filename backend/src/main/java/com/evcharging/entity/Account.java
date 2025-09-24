package com.evcharging.entity;

import com.evcharging.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

@Entity
@Table(
        name = "account",
        indexes = {
                @Index(name = "idx_account_email", columnList = "email", unique = true),
                @Index(name = "idx_account_phone", columnList = "phone", unique = true)
        }
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Account implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email @NotBlank
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Pattern(regexp = "^[0-9]{10,11}$", message = "Phone number should be 10-11 digits")
    @Column(nullable = false, length = 11)
    private String phone;

    @NotBlank @Size(min = 6)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @NotBlank @Size(min = 2, max = 100)
    @Column(nullable = false, length = 100)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Role role;

    @Builder.Default private boolean accountNonExpired = true;
    @Builder.Default private boolean accountNonLocked = true;
    @Builder.Default private boolean credentialsNonExpired = true;
    @Builder.Default private boolean enabled = true;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Role safeRole = (role != null) ? role : Role.EV_DRIVER;
        return safeRole.getGrantedAuthorities();
    }

    @Override public String getUsername() { return email; }
    @Override public boolean isAccountNonExpired() { return accountNonExpired; }
    @Override public boolean isAccountNonLocked() { return accountNonLocked; }
    @Override public boolean isCredentialsNonExpired() { return credentialsNonExpired; }
    @Override public boolean isEnabled() { return enabled; }

    @PrePersist @PreUpdate
    private void normalize() {
        if(email != null) email = email.trim().toLowerCase();
        if(fullName != null) fullName = fullName.trim();
        if(phone != null) phone = phone.trim();
    }

}
