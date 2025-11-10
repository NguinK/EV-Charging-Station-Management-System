//package com.evcharging.scheduler;
//
//import com.evcharging.entity.UserSubscription;
//import com.evcharging.repository.UserSubscriptionRepository;
//import com.evcharging.service.SubscriptionService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//import java.time.OffsetDateTime;
//import java.util.List;
//
/// **
// * Scheduler tự động gia hạn subscription
// */
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class SubscriptionRenewalScheduler {
//
//    private final UserSubscriptionRepository subscriptionRepo;
//    private final SubscriptionService subscriptionService;
//
//    /**
//     * Tự động gia hạn subscription (chạy mỗi ngày lúc 00:00)
//     */
//    @Scheduled(cron = "0 0 0 * * *") // 00:00 mỗi ngày
//    @Transactional
//    public void autoRenewSubscriptions() {
//        log.info("Starting auto-renewal process...");
//
//        // Tìm các subscription cần gia hạn (hết hạn trong 1 ngày tới và có autoRenew = true)
//        OffsetDateTime tomorrow = OffsetDateTime.now().plusDays(1);
//        List<UserSubscription> subscriptionsToRenew =
//                subscriptionRepo.findSubscriptionsForRenewal(tomorrow);
//
//        int successCount = 0;
//        int failCount = 0;
//
//        for (UserSubscription subscription : subscriptionsToRenew) {
//            try {
//                subscriptionService.renewSubscription(subscription.getId());
//                successCount++;
//                log.info("Auto-renewed subscription: {}", subscription.getId());
//            } catch (Exception e) {
//                failCount++;
//                log.error("Failed to auto-renew subscription {}: {}",
//                        subscription.getId(), e.getMessage());
//
//                // TODO: Gửi thông báo cho user về việc gia hạn thất bại
//                // Có thể do hết tiền trong ví
//            }
//        }
//
//        log.info("Auto-renewal completed. Success: {}, Failed: {}", successCount, failCount);
//    }
//
//    /**
//     * Gửi nhắc nhở trước khi subscription hết hạn (chạy mỗi ngày lúc 09:00)
//     */
//    @Scheduled(cron = "0 0 9 * * *") // 09:00 mỗi ngày
//    public void sendExpiryReminders() {
//        log.info("Sending subscription expiry reminders...");
//
//        OffsetDateTime now = OffsetDateTime.now();
//        OffsetDateTime threeDaysLater = now.plusDays(3);
//
//        // Tìm các subscription sẽ hết hạn trong 3 ngày tới
//        List<UserSubscription> expiringSubscriptions =
//                subscriptionRepo.findSubscriptionsForRenewal(threeDaysLater);
//
//        for (UserSubscription subscription : expiringSubscriptions) {
//            if (!subscription.getAutoRenew()) {
//                long daysRemaining = java.time.Duration
//                        .between(now, subscription.getEndDate())
//                        .toDays();
//
//                log.info("Sending expiry reminder to account {}: {} days remaining",
//                        subscription.getAccount().getId(), daysRemaining);
//
//                // TODO: Gửi email/notification nhắc nhở user gia hạn
//            }
//        }
//    }
//}