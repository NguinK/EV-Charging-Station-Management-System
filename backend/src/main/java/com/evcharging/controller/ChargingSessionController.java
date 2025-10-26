package com.evcharging.controller;

import com.evcharging.dto.ChargingSessionDTO;
import com.evcharging.dto.ChargingStatusDTO;
import com.evcharging.dto.EndSessionCreateDTO;
import com.evcharging.entity.ChargingSession;
import com.evcharging.repository.ChargingSessionRepository;
import com.evcharging.service.ChargingSessionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sessions")
public class ChargingSessionController {

    private final ChargingSessionService sessionService;
    private final ChargingSessionRepository sessionRepository;

    public ChargingSessionController(ChargingSessionService sessionService, ChargingSessionRepository sessionRepository) {
        this.sessionService = sessionService;
        this.sessionRepository=sessionRepository    ;
    }

    // Bắt đầu phiên sạc (tài xế quét QR)
    @PostMapping("/start/{reservationId}")
    public ResponseEntity<ChargingSessionDTO> startSession(
            @PathVariable Long reservationId,
            @RequestParam int startSoc) {
        return ResponseEntity.ok(sessionService.startSession(reservationId, startSoc));
    }


    // Kết thúc phiên sạc
    @PutMapping("/{sessionId}/end")
    public ResponseEntity<ChargingSessionDTO> endSession(
            @PathVariable Long sessionId,
            @RequestBody EndSessionCreateDTO req) {
        return ResponseEntity.ok(sessionService.endSession(sessionId, req.getEndSoc(), req.getEnergy(), req.getCost()));
    }

    @GetMapping("/sessions/{id}/status")
    public ChargingStatusDTO getSessionStatus(@PathVariable Long id) {
        ChargingSession session = sessionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));
        return new ChargingStatusDTO(
                session.getId(),
                session.getEndSoc(),
                session.getEnergyConsumed(),
                session.getCost(),
                session.getStatus()
        );
    }

}
