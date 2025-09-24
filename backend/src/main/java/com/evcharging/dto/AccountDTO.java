package com.evcharging.dto;

import com.evcharging.enums.Role;
import lombok.Data;

@Data
public class AccountDTO {
    public long userId;
    public String email;
    public String phone;
    public String fullName;
    public Role role;
    public String token;
}
