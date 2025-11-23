package com.evcharging.controller;

import com.evcharging.dto.SubPlanCreateDTO;
import com.evcharging.dto.SubPlanResponseDTO;
import com.evcharging.dto.UserSubCreateDTO;
import com.evcharging.dto.UserSubResponseDTO;
import com.evcharging.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    // 1. Tạo gói thuê bao mới (admin)
    @PostMapping("/create")
    public ResponseEntity<SubPlanResponseDTO> createPlan(@RequestBody SubPlanCreateDTO dto) {
        SubPlanResponseDTO response = subscriptionService.createPlan(dto);
        return ResponseEntity.ok(response);
    }

    // 2. Đăng ký gói thuê bao cho user
    @PostMapping("/register/{accountId}")
    public ResponseEntity<UserSubResponseDTO> registerSubscription(
            @PathVariable Long accountId,
            @RequestBody UserSubCreateDTO dto) {
        UserSubResponseDTO response = subscriptionService.registerSubscription(accountId, dto);
        return ResponseEntity.ok(response);
    }
}
