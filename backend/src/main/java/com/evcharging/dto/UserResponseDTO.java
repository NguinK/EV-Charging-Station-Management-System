package com.evcharging.dto;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class UserResponseDTO {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String role;

}

