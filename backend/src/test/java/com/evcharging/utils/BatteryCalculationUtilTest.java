package com.evcharging.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for BatteryCalculationUtil.
 * Validates battery-related calculations including SoC conversions and energy calculations.
 */
class BatteryCalculationUtilTest {

    private static final double BATTERY_CAPACITY = 75.0; // 75 kWh battery
    private static final double DELTA = 0.01; // Tolerance for double comparisons

    @Test
    void testCalculateNewSoc_normalCharging() {
        // Starting at 20%, adding 30 kWh to a 75 kWh battery
        // Current energy: 20% of 75 = 15 kWh
        // New energy: 15 + 30 = 45 kWh
        // New SoC: 45/75 = 60%
        int result = BatteryCalculationUtil.calculateNewSoc(20, 30.0, BATTERY_CAPACITY);
        assertEquals(60, result);
    }

    @Test
    void testCalculateNewSoc_cappedAt100() {
        // Starting at 80%, adding 30 kWh to a 75 kWh battery
        // Would result in over 100%, should cap at 100%
        int result = BatteryCalculationUtil.calculateNewSoc(80, 30.0, BATTERY_CAPACITY);
        assertEquals(100, result);
    }

    @Test
    void testCalculateNewSoc_invalidSocNegative() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> BatteryCalculationUtil.calculateNewSoc(-10, 10.0, BATTERY_CAPACITY)
        );
        assertTrue(exception.getMessage().contains("State of charge must be between 0 and 100"));
    }

    @Test
    void testCalculateNewSoc_invalidSocOver100() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> BatteryCalculationUtil.calculateNewSoc(150, 10.0, BATTERY_CAPACITY)
        );
        assertTrue(exception.getMessage().contains("State of charge must be between 0 and 100"));
    }

    @Test
    void testCalculateNewSoc_invalidBatteryCapacity() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> BatteryCalculationUtil.calculateNewSoc(50, 10.0, 0.0)
        );
        assertTrue(exception.getMessage().contains("Battery capacity must be positive"));
    }

    @Test
    void testCalculateNewSoc_invalidBatteryCapacityNegative() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> BatteryCalculationUtil.calculateNewSoc(50, 10.0, -50.0)
        );
        assertTrue(exception.getMessage().contains("Battery capacity must be positive"));
    }

    @Test
    void testCalculateNewSoc_negativeEnergyAdded() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> BatteryCalculationUtil.calculateNewSoc(50, -10.0, BATTERY_CAPACITY)
        );
        assertTrue(exception.getMessage().contains("Energy added cannot be negative"));
    }

    @Test
    void testCalculateEnergyNeeded() {
        // From 20% to 80% on a 75 kWh battery
        // Energy needed: 60% of 75 = 45 kWh
        double result = BatteryCalculationUtil.calculateEnergyNeeded(20, 80, BATTERY_CAPACITY);
        assertEquals(45.0, result, DELTA);
    }

    @Test
    void testCalculateEnergyNeeded_fullCharge() {
        // From 0% to 100%
        double result = BatteryCalculationUtil.calculateEnergyNeeded(0, 100, BATTERY_CAPACITY);
        assertEquals(BATTERY_CAPACITY, result, DELTA);
    }

    @Test
    void testCalculateEnergyNeeded_sameLevel() {
        // From 50% to 50% - no energy needed
        double result = BatteryCalculationUtil.calculateEnergyNeeded(50, 50, BATTERY_CAPACITY);
        assertEquals(0.0, result, DELTA);
    }

    @Test
    void testCalculateEnergyNeeded_targetLowerThanCurrent() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> BatteryCalculationUtil.calculateEnergyNeeded(80, 50, BATTERY_CAPACITY)
        );
        assertTrue(exception.getMessage().contains("Target SoC"));
        assertTrue(exception.getMessage().contains("cannot be less than current SoC"));
    }

    @Test
    void testEnergyToSocPercent() {
        // 37.5 kWh in a 75 kWh battery = 50%
        int result = BatteryCalculationUtil.energyToSocPercent(37.5, BATTERY_CAPACITY);
        assertEquals(50, result);
    }

    @Test
    void testEnergyToSocPercent_fullBattery() {
        // Full battery capacity = 100%
        int result = BatteryCalculationUtil.energyToSocPercent(BATTERY_CAPACITY, BATTERY_CAPACITY);
        assertEquals(100, result);
    }

    @Test
    void testEnergyToSocPercent_emptyBattery() {
        // 0 kWh = 0%
        int result = BatteryCalculationUtil.energyToSocPercent(0.0, BATTERY_CAPACITY);
        assertEquals(0, result);
    }

    @Test
    void testEnergyToSocPercent_overCapacity() {
        // More energy than capacity - should still calculate percentage over 100
        int result = BatteryCalculationUtil.energyToSocPercent(150.0, BATTERY_CAPACITY);
        assertEquals(200, result); // 150/75 = 2.0 = 200%
    }

    @Test
    void testEnergyToSocPercent_negativeEnergy() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> BatteryCalculationUtil.energyToSocPercent(-10.0, BATTERY_CAPACITY)
        );
        assertTrue(exception.getMessage().contains("Energy cannot be negative"));
    }

    @Test
    void testSocPercentToEnergy() {
        // 50% of 75 kWh = 37.5 kWh
        double result = BatteryCalculationUtil.socPercentToEnergy(50, BATTERY_CAPACITY);
        assertEquals(37.5, result, DELTA);
    }

    @Test
    void testSocPercentToEnergy_fullBattery() {
        // 100% = full capacity
        double result = BatteryCalculationUtil.socPercentToEnergy(100, BATTERY_CAPACITY);
        assertEquals(BATTERY_CAPACITY, result, DELTA);
    }

    @Test
    void testSocPercentToEnergy_emptyBattery() {
        // 0% = 0 kWh
        double result = BatteryCalculationUtil.socPercentToEnergy(0, BATTERY_CAPACITY);
        assertEquals(0.0, result, DELTA);
    }

    @Test
    void testSocPercentToEnergy_invalidSoc() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> BatteryCalculationUtil.socPercentToEnergy(150, BATTERY_CAPACITY)
        );
        assertTrue(exception.getMessage().contains("State of charge must be between 0 and 100"));
    }

    @Test
    void testRoundTripConversion() {
        // Test that converting SoC -> Energy -> SoC gives back the original value
        int originalSoc = 65;
        double energy = BatteryCalculationUtil.socPercentToEnergy(originalSoc, BATTERY_CAPACITY);
        int convertedBackSoc = BatteryCalculationUtil.energyToSocPercent(energy, BATTERY_CAPACITY);
        assertEquals(originalSoc, convertedBackSoc);
    }
}
