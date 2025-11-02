//package com.evcharging.service;
//
//import com.evcharging.entity.*;
//import com.evcharging.enums.PlanStatus;
//import com.evcharging.enums.SubscriptionStatus;
//import com.evcharging.repository.*;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class SubscriptionService {
//
//    private final SubscriptionPlanRepository planRepo;
//    private final UserSubscriptionRepository userSubscriptionRepo;
//    private final AccountRepository accountRepo;
//    private final WalletService walletService;
//
//    /**
//     * Lấy tất cả gói thuê bao đang active
//     */
//    public List<SubscriptionPlan> getAllActivePlans() {
//        return planRepo.findByStatus(PlanStatus.ACTIVE);
//    }
//
//    /**
//     * Đăng ký gói thuê bao
//     */
//    @Transactional
//    public UserSubscription subscribe(Long accountId, Long planId, boolean autoRenew) {
//        log.info("Account {} subscribing to plan {}", accountId, planId);
//
//        Account account = accountRepo.findById(accountId)
//                .orElseThrow(() -> new RuntimeException("Account not found"));
//
//        SubscriptionPlan plan = planRepo.findById(planId)
//                .orElseThrow(() -> new RuntimeException("Plan not found"));
//
//        if (plan.getStatus() != PlanStatus.ACTIVE) {
//            throw new RuntimeException("Plan is not active");
//        }
//
//        // Kiểm tra đã có subscription active chưa
//        userSubscriptionRepo.findActiveSubscription(accountId, LocalDateTime.now())
//                .ifPresent(sub -> {
//                    throw new RuntimeException("User already has an active subscription");
//                });
//
//        // Trừ tiền từ ví
//        walletService.deductBalance(
//                accountId,
//                plan.getMonthlyFee(),
//                "Subscription payment: " + plan.getName());
//
//        // Tạo subscription
//        LocalDateTime startDate = LocalDateTime.now();
//        LocalDateTime endDate = startDate.plusMonths(1);
//
//        UserSubscription subscription = new UserSubscription();
//        subscription.setAccount(account);
//        subscription.setPlan(plan);
//        subscription.setStartDate(startDate);
//        subscription.setEndDate(endDate);
//        subscription.setStatus(SubscriptionStatus.ACTIVE);
//        subscription.setUsedMinutes(0.0);
//        subscription.setUsedKwh(0.0);
//        subscription.setAutoRenew(autoRenew);
//
//        subscription = userSubscriptionRepo.save(subscription);
//
//        log.info("Subscription created: {}", subscription.getId());
//
//        return subscription;
//    }
//
//    /**
//     * Hủy subscription
//     */
//    @Transactional
//    public UserSubscription cancelSubscription(Long subscriptionId) {
//        log.info("Cancelling subscription: {}", subscriptionId);
//
//        UserSubscription subscription = userSubscriptionRepo.findById(subscriptionId)
//                .orElseThrow(() -> new RuntimeException("Subscription not found"));
//
//        if (subscription.getStatus() != SubscriptionStatus.ACTIVE) {
//            throw new RuntimeException("Subscription is not active");
//        }
//
//        subscription.setStatus(SubscriptionStatus.CANCELLED);
//        subscription.setAutoRenew(false);
//
//        subscription = userSubscriptionRepo.save(subscription);
//
//        log.info("Subscription cancelled: {}", subscriptionId);
//
//        return subscription;
//    }
//
//    /**
//     * Gia hạn subscription tự động
//     */
//    @Transactional
//    public UserSubscription renewSubscription(Long subscriptionId) {
//        log.info("Renewing subscription: {}", subscriptionId);
//
//        UserSubscription oldSubscription = userSubscriptionRepo.findById(subscriptionId)
//                .orElseThrow(() -> new RuntimeException("Subscription not found"));
//
//        SubscriptionPlan plan = oldSubscription.getPlan();
//
//        // Trừ tiền
//        walletService.deductBalance(
//                oldSubscription.getAccount().getId(),
//                plan.getMonthlyFee(),
//                "Subscription renewal: " + plan.getName());
//
//        // Tạo subscription mới
//        LocalDateTime startDate = oldSubscription.getEndDate();
//        LocalDateTime endDate = startDate.plusMonths(1);
//
//        UserSubscription newSubscription = new UserSubscription();
//        newSubscription.setAccount(oldSubscription.getAccount());
//        newSubscription.setPlan(plan);
//        newSubscription.setStartDate(startDate);
//        newSubscription.setEndDate(endDate);
//        newSubscription.setStatus(SubscriptionStatus.ACTIVE);
//        newSubscription.setUsedMinutes(0.0);
//        newSubscription.setUsedKwh(0.0);
//        newSubscription.setAutoRenew(oldSubscription.getAutoRenew());
//
//        newSubscription = userSubscriptionRepo.save(newSubscription);
//
//        // Update old subscription
//        oldSubscription.setStatus(SubscriptionStatus.EXPIRED);
//        userSubscriptionRepo.save(oldSubscription);
//
//        log.info("Subscription renewed: {}", newSubscription.getId());
//
//        return newSubscription;
//    }
//
//    /**
//     * Cập nhật usage (được gọi sau mỗi phiên sạc)
//     */
//    @Transactional
//    public void updateUsage(Long accountId, double minutes, double kwh) {
//        userSubscriptionRepo.findActiveSubscription(accountId, LocalDateTime.now())
//                .ifPresent(subscription -> {
//                    subscription.setUsedMinutes(subscription.getUsedMinutes() + minutes);
//                    subscription.setUsedKwh(subscription.getUsedKwh() + kwh);
//                    userSubscriptionRepo.save(subscription);
//
//                    log.debug("Updated subscription usage for account {}: +{} min, +{} kWh",
//                            accountId, minutes, kwh);
//                });
//    }
//
//    /**
//     * Kiểm tra còn quota không (cho free minutes/kwh)
//     */
//    public boolean hasQuota(Long accountId, double minutes, double kwh) {
//        return userSubscriptionRepo.findActiveSubscription(accountId, LocalDateTime.now())
//                .map(subscription -> {
//                    SubscriptionPlan plan = subscription.getPlan();
//
//                    boolean hasMinutes = (plan.getFreeMinutesPerMonth() == null) ||
//                            (subscription.getUsedMinutes() + minutes <= plan.getFreeMinutesPerMonth());
//
//                    boolean hasKwh = (plan.getFreeKwhPerMonth() == null) ||
//                            (subscription.getUsedKwh() + kwh <= plan.getFreeKwhPerMonth());
//
//                    return hasMinutes && hasKwh;
//                })
//                .orElse(false);
//    }
//
//    /**
//     * Lấy subscription active của user
//     */
//    public UserSubscription getActiveSubscription(Long accountId) {
//        return userSubscriptionRepo.findActiveSubscription(accountId, LocalDateTime.now())
//                .orElse(null);
//    }
//
//    /**
//     * Lấy lịch sử subscription của user
//     */
//    public List<UserSubscription> getSubscriptionHistory(Long accountId) {
//        return userSubscriptionRepo.findByAccountId(accountId);
//    }
//}