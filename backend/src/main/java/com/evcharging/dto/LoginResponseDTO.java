package com.evcharging.dto;

import lombok.Data;

@Data
public class LoginResponseDTO {
    private String token;
    private String fullName;
}