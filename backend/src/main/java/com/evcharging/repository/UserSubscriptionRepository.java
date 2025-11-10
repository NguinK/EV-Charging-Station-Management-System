package com.evcharging.repository;

import com.evcharging.entity.UserSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, Long> {

    List<UserSubscription> findByAccountId(Long accountId);

    @Query("SELECT us FROM UserSubscription us WHERE us.account.id = :accountId " +
            "AND us.status = 'ACTIVE' AND us.endDate > :now")
    Optional<UserSubscription> findActiveSubscription(
            @Param("accountId") Long accountId,
            @Param("now") OffsetDateTime now);

    @Query("SELECT us FROM UserSubscription us WHERE us.status = 'ACTIVE' " +
            "AND us.autoRenew = true AND us.endDate <= :endDate")
    List<UserSubscription> findSubscriptionsForRenewal(@Param("endDate") OffsetDateTime endDate);
}