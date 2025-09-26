package com.evcharging.dto;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class UserCreateDTO {
    private String fullName;
    private String email;
    private String phone;
    private String password;
    private String role; // ADMIN, EV_DRIVER, CS_STAFF
}

