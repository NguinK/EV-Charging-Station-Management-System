//package com.evcharging.utils;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import java.math.BigDecimal;
//import java.math.RoundingMode;
//
//@Component
//public class PriceCalculator {
//    @Value("${charging.price.per-kwh:0.30}")
//    private Double basePrice;
//
//    @Value("${charging.service.fee:1.50}")
//    private Double serviceFee;
//
//    @Value("${reservation.penalty.amount:10.00}")
//    private Double penaltyAmount;
//
//    //Formula: (energyUsedKwh * basePricePerKwh) + serviceFee
//    public BigDecimal calculateChargingCost(BigDecimal energyUsedKwh) {
//        if (energyUsedKwh == null || energyUsedKwh.compareTo(BigDecimal.ZERO) <= 0) {
//            return BigDecimal.ZERO;
//        }
//
//        BigDecimal energyCost = energyUsedKwh.multiply(BigDecimal.valueOf(basePrice));
//        BigDecimal totalCost = energyCost.add(BigDecimal.valueOf(serviceFee));
//
//        return totalCost.setScale(2, RoundingMode.HALF_UP);
//    }
//
//    //Get the penalty amount for no-show reservations
//    public BigDecimal getPenaltyAmount() {
//        return BigDecimal.valueOf(penaltyAmount).setScale(2, RoundingMode.HALF_UP);
//    }
//
//    /**
//     * Calculate reservation hold fee (if applicable)
//     */
//    public BigDecimal calculateReservationFee(Long durationMinutes) {
//        // Simple logic: $2 per hour or fraction thereof
//        double hours = Math.ceil(durationMinutes / 60.0);
//        return BigDecimal.valueOf(hours * 2.0).setScale(2, RoundingMode.HALF_UP);
//    }
//}
