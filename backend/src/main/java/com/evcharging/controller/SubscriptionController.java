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

    //  Tạo gói thuê bao mới (admin)
    @PostMapping("/adminCreate")
    public ResponseEntity<SubPlanResponseDTO> createPlan(@RequestBody SubPlanCreateDTO dto) {
        SubPlanResponseDTO response = subscriptionService.createPlan(dto);
        return ResponseEntity.ok(response);
    }

    //  Cập nhật gói thuê bao
    @PutMapping("/adminUpdate/{id}")
    public ResponseEntity<SubPlanResponseDTO> updatePlan(@PathVariable Long id,
                                                         @RequestBody SubPlanCreateDTO dto) {
        SubPlanResponseDTO response = subscriptionService.updatePlan(id, dto);
        return ResponseEntity.ok(response);
    }

    //  Xóa gói thuê bao
    @DeleteMapping("/adminDelete/{id}")
    public ResponseEntity<Void> deletePlan(@PathVariable Long id) {
        subscriptionService.deletePlan(id);
        return ResponseEntity.noContent().build();
    }
    // Admin lấy tất cả subscriptions của mọi driver
    @GetMapping("/adminGetAllDriverSubs")
    public ResponseEntity<List<UserSubResponseDTO>> getAllDriverSubscriptions() {
        List<UserSubResponseDTO> response = subscriptionService.getAllDriverSubscriptions();
        return ResponseEntity.ok(response);
    }


    // 4. Lấy tất cả gói thuê bao
    @GetMapping("/adminGetAllSubs")
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

    // Driver lấy lịch sử subscription của mình
    @GetMapping("/driverGetSubHistory/{accountId}")
    public ResponseEntity<List<UserSubResponseDTO>> getSubscriptionHistory(@PathVariable Long accountId) {
        List<UserSubResponseDTO> response = subscriptionService.getSubscriptionHistory(accountId);
        return ResponseEntity.ok(response);
    }


}
