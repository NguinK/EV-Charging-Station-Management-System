package com.evcharging.controller;

import com.evcharging.entity.ChargingSession;
import com.evcharging.service.ChargingSessionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sessions")
public class ChargingSessionController {

    private final ChargingSessionService sessionService;

    public ChargingSessionController(ChargingSessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping("/start/{reservationId}")
    public ResponseEntity<ChargingSession> startSession(
            @PathVariable Long reservationId,
            @RequestParam int startSoc) {
        return ResponseEntity.ok(sessionService.startSession(reservationId, startSoc));
    }

    @PostMapping("/end/{sessionId}")
    public ResponseEntity<ChargingSession> endSession(
            @PathVariable Long sessionId,
            @RequestParam int endSoc,
            @RequestParam double energy,
            @RequestParam double cost) {
        return ResponseEntity.ok(sessionService.endSession(sessionId, endSoc, energy, cost));
    }
}