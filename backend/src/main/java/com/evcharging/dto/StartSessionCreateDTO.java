package com.evcharging.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StartSessionCreateDTO {
    private Long reservationId;
    private int startSoc;
}
