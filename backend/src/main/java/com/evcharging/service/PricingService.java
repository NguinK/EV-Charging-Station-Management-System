package com.evcharging.service;

import com.evcharging.entity.ChargingPoint;
import com.evcharging.entity.ChargingSession;
import com.evcharging.entity.Reservation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PricingService {

    private final SystemConfigurationService configService;
    public BigDecimal calculateLiveChargingFee(ChargingSession session, OffsetDateTime now) {
        if (session == null || session.getStartTime() == null) {
            throw new IllegalArgumentException("Invalid charging session");
        }

        ChargingPoint point = session.getChargingPoint();
        if (point == null) {
            throw new IllegalArgumentException("Charging point is required");
        }

        BigDecimal energyFee = calculateEnergyFee(session.getEnergyConsumed(), point.getPricePerKwh());
        BigDecimal timeFee = calculateTimeFee(session.getStartTime(), now, point.getPricePerMinute());
        BigDecimal serviceFee = getServiceFee();

        return energyFee.add(timeFee).add(serviceFee).setScale(2, RoundingMode.HALF_UP);
    }
    //Tính tổng phí sạc
    public BigDecimal calculateChargingFee(ChargingSession session) {
        if (session == null || session.getStartTime() == null || session.getEndTime() == null) {
            throw new IllegalArgumentException("Invalid charging session");
        }

        ChargingPoint point = session.getChargingPoint();
        if (point == null) {
            throw new IllegalArgumentException("Charging point is required");
        }

        //1. Energy Fee
        BigDecimal energyFee = calculateEnergyFee(
                session.getEnergyConsumed(),
                point.getPricePerKwh()
        );

        //2. Time-based Fee
        BigDecimal timeFee = calculateTimeFee(
                session.getStartTime(),
                session.getEndTime(),
                point.getPricePerMinute()
        );

        //3. Service Fee
        BigDecimal serviceFeeAmount = getServiceFee();

        //4. Reservation Fee
        BigDecimal reservationFeeAmount = BigDecimal.ZERO;
        if (session.getReservation() != null) {
            reservationFeeAmount = calculateReservationHoldFee(session.getReservation());
        }

        // Tổng cộng
        BigDecimal totalFee = energyFee
                .add(timeFee)
                .add(serviceFeeAmount)
                .add(reservationFeeAmount);

        log.info("Charging fee breakdown - Energy: {}, Time: {}, Service: {}, Reservation: {}, Total: {}",
                energyFee, timeFee, serviceFeeAmount, reservationFeeAmount, totalFee);

        return totalFee.setScale(2, RoundingMode.HALF_UP);
    }

    //Tính phí năng lượng đơn giản
    public BigDecimal calculateEnergyFee(Double energyKwh, Double pricePerKwh) {
        if (energyKwh == null || energyKwh <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal energy = BigDecimal.valueOf(energyKwh);
        BigDecimal price = BigDecimal.valueOf(pricePerKwh != null ? pricePerKwh : 0.30);

        return energy.multiply(price).setScale(2, RoundingMode.HALF_UP);
    }

    //Tính phí thời gian sạc
    public BigDecimal calculateTimeFee(
            java.time.OffsetDateTime startTime,
            java.time.OffsetDateTime endTime,
            Double pricePerMinute) {

        if (startTime == null || endTime == null) {
            return BigDecimal.ZERO;
        }

        long minutes = Duration.between(startTime, endTime).toMinutes();
        BigDecimal price = BigDecimal.valueOf(pricePerMinute != null ? pricePerMinute : 0.0);

        return BigDecimal.valueOf(minutes).multiply(price).setScale(2, RoundingMode.HALF_UP);
    }

    //Tính phí giữ chỗ đặt trước (lấy từ DB, 10,000VND/giờ)
    public BigDecimal calculateReservationHoldFee(Reservation reservation) {
        if (reservation == null || reservation.getStartTime() == null
                || reservation.getExpireTime() == null) {
            return BigDecimal.ZERO;
        }

        //Tính tổng số phút
        long totalMinutes = Duration.between(
                reservation.getStartTime(),
                reservation.getExpireTime()
        ).toMinutes();

        //Làm tròn lên số giờ
        long hours = (long) Math.ceil(totalMinutes / 60.0);

        //LẤY PHÍ TỪ DB (mặc định 10,000 VND)
        BigDecimal feePerHour = configService.getConfigValueAsDecimal(
                SystemConfigurationService.RESERVATION_HOLD_FEE_PER_HOUR,
                BigDecimal.valueOf(10000)
        );

        BigDecimal totalFee = BigDecimal.valueOf(hours)
                .multiply(feePerHour)
                .setScale(2, RoundingMode.HALF_UP);

        log.debug("Reservation hold fee: {} hours x {} VND = {} VND",
                hours, feePerHour, totalFee);

        return totalFee;
    }

    //Phí phạt no-show - lấy từ DB
    public BigDecimal getPenaltyAmount() {
        BigDecimal penalty = configService.getConfigValueAsDecimal(
                SystemConfigurationService.RESERVATION_PENALTY_AMOUNT,
                BigDecimal.valueOf(10000)
        );

        return penalty.setScale(2, RoundingMode.HALF_UP);
    }

    //Phí dịch vụ - lấy từ DB
    public BigDecimal getServiceFee() {
        BigDecimal serviceFee = configService.getConfigValueAsDecimal(
                SystemConfigurationService.CHARGING_SERVICE_FEE,
                BigDecimal.valueOf(15000)
        );

        return serviceFee.setScale(2, RoundingMode.HALF_UP);
    }

    //Tính phí giữ chỗ đặt trước theo thời gian
    public BigDecimal calculateReservationFee(Long durationMinutes) {
        if (durationMinutes == null || durationMinutes <= 0) {
            return BigDecimal.ZERO;
        }

        long hours = (long) Math.ceil(durationMinutes / 60.0);

        BigDecimal feePerHour = configService.getConfigValueAsDecimal(
                SystemConfigurationService.RESERVATION_HOLD_FEE_PER_HOUR,
                BigDecimal.valueOf(10000)
        );

        return BigDecimal.valueOf(hours)
                .multiply(feePerHour)
                .setScale(2, RoundingMode.HALF_UP);
    }
}