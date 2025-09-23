package com.evcharging.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AdminDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long id;
    private String email;
    private String phone;
    private String fullName;
}
