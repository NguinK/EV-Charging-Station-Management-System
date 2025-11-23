package com.evcharging.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class UserSubCreateDTO {
    @NotBlank(message = "Plan name is required")
    private String planName;

    @NotNull(message = "AutoRenew flag is required")
    private Boolean autoRenew;
}
