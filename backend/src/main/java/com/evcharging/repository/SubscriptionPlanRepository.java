
package com.evcharging.repository;

import com.evcharging.entity.SubscriptionPlan;

import com.evcharging.enums.PlanStatus;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Long> {

    List<SubscriptionPlan> findByStatus(PlanStatus status);

    Optional<SubscriptionPlan> findByName(String name);
}