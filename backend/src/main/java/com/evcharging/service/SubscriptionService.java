package com.evcharging.service;

import com.evcharging.dto.*;
import com.evcharging.entity.*;
import com.evcharging.enums.SubscriptionStatus;
import com.evcharging.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionPlanRepository planRepo;
    private final UserSubscriptionRepository userSubscriptionRepo;
    private final AccountRepository accountRepo;
    private final WalletService walletService;
    private final WalletRepository walletRepo;
    private final SubscriptionPlanRepository subscriptionPlanRepo;
    private final DtoMapper dtoMapper;

    public SubPlanResponseDTO createPlan(SubPlanCreateDTO dto) {
        if (subscriptionPlanRepo.existsByName(dto.getName())) {
            throw new IllegalArgumentException("Plan name already exists");
        }

        SubscriptionPlan plan = new SubscriptionPlan();
        plan.setName(dto.getName());
        plan.setDiscountPercent(dto.getDiscountPercent());
        plan.setPrice(dto.getPrice());

        SubscriptionPlan saved = subscriptionPlanRepo.save(plan);
        return new SubPlanResponseDTO(saved.getId(), saved.getName(), saved.getDiscountPercent(),saved.getCreatedAt(),saved.getPrice());
    }

    @Transactional
    public SubPlanResponseDTO updatePlan(Long id, SubPlanCreateDTO dto) {
        SubscriptionPlan plan = subscriptionPlanRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Plan not found"));

        // Nếu đổi tên, check trùng
        if (!plan.getName().equals(dto.getName())
                && subscriptionPlanRepo.existsByName(dto.getName())) {
            throw new IllegalArgumentException("Plan name already exists");
        }

        plan.setName(dto.getName());
        plan.setDiscountPercent(dto.getDiscountPercent());
        plan.setPrice(dto.getPrice());

        SubscriptionPlan updated = subscriptionPlanRepo.save(plan);
        return new SubPlanResponseDTO(
                updated.getId(),
                updated.getName(),
                updated.getDiscountPercent(),
                updated.getCreatedAt(),
                updated.getPrice()
        );
    }

    @Transactional
    public void deletePlan(Long id) {
        if (!subscriptionPlanRepo.existsById(id)) {
            throw new IllegalArgumentException("Plan not found");
        }
        subscriptionPlanRepo.deleteById(id);
    }

    public List<SubPlanResponseDTO> getAllPlans() {
        List<SubscriptionPlan> plans = subscriptionPlanRepo.findAll();
        return plans.stream()
                .map(plan -> new SubPlanResponseDTO(
                        plan.getId(),
                        plan.getName(),
                        plan.getDiscountPercent(),
                        plan.getCreatedAt(),
                        plan.getPrice()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public UserSubResponseDTO registerSubscription(Long accountId, UserSubCreateDTO dto) {
        // 1. Kiểm tra account
        Account account = accountRepo.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        // 2. Kiểm tra plan theo name
        SubscriptionPlan plan = subscriptionPlanRepo.findByName(dto.getPlanName())
                .orElseThrow(() -> new IllegalArgumentException("Subscription plan not found"));

        // 3. Kiểm tra driver đã có gói active chưa
        boolean hasActive = userSubscriptionRepo.existsByAccountAndStatus(account, SubscriptionStatus.ACTIVE);
        if (hasActive) {
            throw new IllegalArgumentException("Driver already has an active subscription");
        }

        // 4. Lấy ví
        Wallet wallet = walletRepo.findByAccountId(account.getId())
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found"));

        // 5. Tính phí gói
        Double subscriptionFee = plan.getPrice();
        if (subscriptionFee == null) {
            throw new IllegalStateException("Subscription plan " + plan.getName() + " does not have a price set");
        }

        // 6. Check số dư (so sánh double)
        if (wallet.getBalance() < subscriptionFee) {
            throw new IllegalArgumentException("Not enough balance for plan " + plan.getName());
        }

        // 7. Trừ tiền (hàm nhận double)
        WalletTransaction tx = walletService.deductBalance(
                accountId,
                subscriptionFee,
                "Đăng ký gói " + plan.getName()
        );

        // 8. Tạo subscription
        UserSubscription subscription = new UserSubscription();
        subscription.setAccount(account);
        subscription.setPlan(plan);
        subscription.setStartDate(OffsetDateTime.now());
        subscription.setEndDate(OffsetDateTime.now().plusMonths(1));
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setAutoRenew(dto.getAutoRenew());

        UserSubscription saved = userSubscriptionRepo.save(subscription);

        // 9. Trả về DTO
        return dtoMapper.toUserSubResponseDTO(saved);
    }

    @Transactional(readOnly = true)
    public UserSubResponseDTO getActiveSubscription(Long accountId) {
        // 1. Kiểm tra account
        Account account = accountRepo.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        // 2. Tìm subscription ACTIVE
        UserSubscription subscription = userSubscriptionRepo
                .findByAccountAndStatus(account, SubscriptionStatus.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException("No active subscription found"));

        // 3. Trả về DTO
        return dtoMapper.toUserSubResponseDTO(subscription);
    }
}