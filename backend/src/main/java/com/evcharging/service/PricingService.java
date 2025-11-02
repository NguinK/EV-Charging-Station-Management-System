package com.evcharging.service;

import com.evcharging.entity.ChargingPoint;
import com.evcharging.entity.ChargingSession;
import com.evcharging.entity.Reservation;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class PricingService {

    public double calculateChargingFee(ChargingSession session) {
        ChargingPoint point = session.getChargingPoint();

        double pricePerKwh = point.getPricePerKwh();
        double pricePerMinute = point.getPricePerMinute();

        // 1. Phí năng lượng
        double energyFee = session.getEnergyConsumed() * pricePerKwh;

        // 2. Phí thời gian sạc
        long minutes = Duration.between(session.getStartTime(), session.getEndTime()).toMinutes();
        double timeFee = minutes * pricePerMinute;

        // 3. Phí giữ chỗ (nếu có reservation)
        double reservationFee = 0;
        if (session.getReservation() != null) {
            Reservation reservation = session.getReservation();
            long hours = Duration.between(
                    reservation.getStartTime(),
                    reservation.getExpireTime()
            ).toHours();
            reservationFee = hours * 10_000; // 10k VND mỗi giờ
        }

        return energyFee + timeFee + reservationFee;
    }
}