package com.project.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.backend.enums.Role;
import lombok.Data;

@Data
public class AdminDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long id;
    private String email;
    private String phone;
    private String fullName;
}
