package com.evcharging.dto.admin;

import com.evcharging.enums.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String email;
    private String phone;
    private String fullName;
    private Role role;
    private AccountStatus status;
    private Boolean enabled;
    private OffsetDateTime createdAt;
}