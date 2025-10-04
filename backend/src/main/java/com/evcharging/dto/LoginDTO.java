package com.evcharging.dto;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
@Data

public class LoginDTO {
    @Email(message = "Email không hợp lệ")
    private String email;
    @NotBlank(message = "Password không được trống")
    private String password;
    public String getEmail() { return email; }
    public void setEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Email không hợp lệ!");
        }
        this.email = email;
    }

    public String getPassword() { return password; }
    public void setPassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password không được trống!");
        }
        this.password = password;
    }
}
