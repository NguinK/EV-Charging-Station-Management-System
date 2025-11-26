package com.evcharging.controller;

import com.evcharging.dto.SubPlanCreateDTO;
import com.evcharging.dto.SubPlanResponseDTO;
import com.evcharging.dto.UserSubCreateDTO;
import com.evcharging.dto.UserSubResponseDTO;
import com.evcharging.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    // 2. Cập nhật gói thuê bao
    @PutMapping("/update/{id}")
    public ResponseEntity<SubPlanResponseDTO> updatePlan(@PathVariable Long id,
                                                         @RequestBody SubPlanCreateDTO dto) {
        SubPlanResponseDTO response = subscriptionService.updatePlan(id, dto);
        return ResponseEntity.ok(response);
    }

    // 3. Xóa gói thuê bao
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deletePlan(@PathVariable Long id) {
        subscriptionService.deletePlan(id);
        return ResponseEntity.noContent().build();
    }

    // 4. Lấy tất cả gói thuê bao
    @GetMapping
    public ResponseEntity<List<SubPlanResponseDTO>> getAllPlans() {
        List<SubPlanResponseDTO> plans = subscriptionService.getAllPlans();
        return ResponseEntity.ok(plans);
    }


    //  Đăng ký gói thuê bao cho user
    @PostMapping("/register/{accountId}")
    public ResponseEntity<UserSubResponseDTO> registerSubscription(
            @PathVariable Long accountId,
            @RequestBody UserSubCreateDTO dto) {
        UserSubResponseDTO response = subscriptionService.registerSubscription(accountId, dto);
        return ResponseEntity.ok(response);
    }
    //  Lấy gói đang ACTIVE của driver
    @GetMapping("/active/{accountId}")
    public ResponseEntity<UserSubResponseDTO> getActiveSubscription(@PathVariable Long accountId) {
        UserSubResponseDTO response = subscriptionService.getActiveSubscription(accountId);
        return ResponseEntity.ok(response);
    }

}
