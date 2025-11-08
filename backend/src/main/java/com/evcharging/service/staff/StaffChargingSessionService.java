package com.evcharging.service.staff;

import com.evcharging.dto.staff.ChargingSessionResponse;
import com.evcharging.dto.staff.StartSessionRequest;
import com.evcharging.dto.staff.StopSessionRequest;

public interface StaffChargingSessionService {
    //Start a new charging session
    ChargingSessionResponse startSession(StartSessionRequest request);

    //Stop a charging session and create transaction
    ChargingSessionResponse stopSession(Long sessionId, StopSessionRequest request);
}
