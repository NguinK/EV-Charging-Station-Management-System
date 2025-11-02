//
//package com.evcharging.controller;
//
//import com.evcharging.entity.SubscriptionPlan;
//import com.evcharging.entity.UserSubscription;
//import com.evcharging.service.SubscriptionService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.web.bind.annotation.*;
//import java.util.List;
//
///**
// * Controller quản lý gói thuê bao
// */
//@RestController
//@RequestMapping("/api/subscriptions")
//@RequiredArgsConstructor
//public class SubscriptionController {
//
//    private final SubscriptionService subscriptionService;
//
//    /**
//     * Lấy danh sách các gói thuê bao
//     * GET /api/subscriptions/plans
//     */
//    @GetMapping("/plans")
//    public ResponseEntity<List<SubscriptionPlan>> getAllPlans() {
//        List<SubscriptionPlan> plans = subscriptionService.getAllActivePlans();
//        return ResponseEntity.ok(plans);
//    }
//
//    /**
//     * Đăng ký gói thuê bao
//     * POST /api/subscriptions/subscribe
//     */
//    @PostMapping("/subscribe")
//    public ResponseEntity<UserSubscription> subscribe(
//            @AuthenticationPrincipal Long accountId,
//            @RequestParam Long planId,
//            @RequestParam(defaultValue = "true") boolean autoRenew) {
//
//        UserSubscription subscription = subscriptionService.subscribe(accountId, planId, autoRenew);
//        return ResponseEntity.ok(subscription);
//    }
//
//    /**
//     * Hủy subscription
//     * POST /api/subscriptions/{subscriptionId}/cancel
//     */
//    @PostMapping("/{subscriptionId}/cancel")
//    public ResponseEntity<UserSubscription> cancel(@PathVariable Long subscriptionId) {
//        UserSubscription subscription = subscriptionService.cancelSubscription(subscriptionId);
//        return ResponseEntity.ok(subscription);
//    }
//
//    /**
//     * Lấy subscription hiện tại
//     * GET /api/subscriptions/active
//     */
//    @GetMapping("/active")
//    public ResponseEntity<UserSubscription> getActiveSubscription(
//            @AuthenticationPrincipal Long accountId) {
//
//        UserSubscription subscription = subscriptionService.getActiveSubscription(accountId);
//        if (subscription == null) {
//            return ResponseEntity.noContent().build();
//        }
//        return ResponseEntity.ok(subscription);
//    }
//
//    /**
//     * Lấy lịch sử subscription
//     * GET /api/subscriptions/history
//     */
//    @GetMapping("/history")
//    public ResponseEntity<List<UserSubscription>> getHistory(
//            @AuthenticationPrincipal Long accountId) {
//
//        List<UserSubscription> subscriptions = subscriptionService.getSubscriptionHistory(accountId);
//        return ResponseEntity.ok(subscriptions);
//    }
//}