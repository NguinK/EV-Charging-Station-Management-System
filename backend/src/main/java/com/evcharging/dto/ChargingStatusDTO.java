package com.evcharging.dto;

import com.evcharging.enums.SessionStatus;

public record ChargingStatusDTO (
    Long sessionId,
    int soc,
    double energy,
    double cost,
    SessionStatus status
){}
