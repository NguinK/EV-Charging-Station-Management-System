package com.evcharging.utils;

/**
 * Utility class for battery-related calculations.
 * Provides methods to convert between battery state of charge (SoC) percentages and energy in kWh.
 * All calculations assume SoC is in the range 0-100% and battery capacity is in kWh.
 */
public class BatteryCalculationUtil {

    private BatteryCalculationUtil() {
        // Private constructor to prevent instantiation
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Calculate new state of charge after adding energy to the battery.
     * 
     * @param currentSocPercent Current battery state of charge in percentage (0-100)
     * @param energyAddedKwh Energy added to the battery in kilowatt-hours (kWh)
     * @param batteryCapacityKwh Total battery capacity in kilowatt-hours (kWh)
     * @return New state of charge in percentage (0-100), capped at 100
     * @throws IllegalArgumentException if inputs are invalid
     */
    public static int calculateNewSoc(int currentSocPercent, double energyAddedKwh, double batteryCapacityKwh) {
        validateSocPercent(currentSocPercent);
        validateBatteryCapacity(batteryCapacityKwh);
        
        if (energyAddedKwh < 0) {
            throw new IllegalArgumentException("Energy added cannot be negative: " + energyAddedKwh);
        }
        
        // Calculate current energy level
        double currentEnergyKwh = socPercentToEnergy(currentSocPercent, batteryCapacityKwh);
        
        // Add the new energy
        double newEnergyKwh = currentEnergyKwh + energyAddedKwh;
        
        // Convert back to percentage and cap at 100%
        int newSocPercent = energyToSocPercent(newEnergyKwh, batteryCapacityKwh);
        return Math.min(100, newSocPercent);
    }

    /**
     * Calculate the energy needed to charge from current SoC to target SoC.
     * 
     * @param currentSocPercent Current battery state of charge in percentage (0-100)
     * @param targetSocPercent Target battery state of charge in percentage (0-100)
     * @param batteryCapacityKwh Total battery capacity in kilowatt-hours (kWh)
     * @return Energy needed in kilowatt-hours (kWh)
     * @throws IllegalArgumentException if inputs are invalid or target is less than current
     */
    public static double calculateEnergyNeeded(int currentSocPercent, int targetSocPercent, double batteryCapacityKwh) {
        validateSocPercent(currentSocPercent);
        validateSocPercent(targetSocPercent);
        validateBatteryCapacity(batteryCapacityKwh);
        
        if (targetSocPercent < currentSocPercent) {
            throw new IllegalArgumentException(
                "Target SoC (" + targetSocPercent + "%) cannot be less than current SoC (" + currentSocPercent + "%)"
            );
        }
        
        double currentEnergyKwh = socPercentToEnergy(currentSocPercent, batteryCapacityKwh);
        double targetEnergyKwh = socPercentToEnergy(targetSocPercent, batteryCapacityKwh);
        
        return targetEnergyKwh - currentEnergyKwh;
    }

    /**
     * Convert energy in kWh to state of charge percentage.
     * 
     * @param energyKwh Energy in kilowatt-hours (kWh)
     * @param batteryCapacityKwh Total battery capacity in kilowatt-hours (kWh)
     * @return State of charge in percentage (0-100)
     * @throws IllegalArgumentException if inputs are invalid
     */
    public static int energyToSocPercent(double energyKwh, double batteryCapacityKwh) {
        validateBatteryCapacity(batteryCapacityKwh);
        
        if (energyKwh < 0) {
            throw new IllegalArgumentException("Energy cannot be negative: " + energyKwh);
        }
        
        double socPercent = (energyKwh / batteryCapacityKwh) * 100.0;
        return (int) Math.round(socPercent);
    }

    /**
     * Convert state of charge percentage to energy in kWh.
     * 
     * @param socPercent State of charge in percentage (0-100)
     * @param batteryCapacityKwh Total battery capacity in kilowatt-hours (kWh)
     * @return Energy in kilowatt-hours (kWh)
     * @throws IllegalArgumentException if inputs are invalid
     */
    public static double socPercentToEnergy(int socPercent, double batteryCapacityKwh) {
        validateSocPercent(socPercent);
        validateBatteryCapacity(batteryCapacityKwh);
        
        return (socPercent / 100.0) * batteryCapacityKwh;
    }

    /**
     * Validate that SoC percentage is in valid range (0-100).
     * 
     * @param socPercent State of charge in percentage
     * @throws IllegalArgumentException if SoC is not in range 0-100
     */
    private static void validateSocPercent(int socPercent) {
        if (socPercent < 0 || socPercent > 100) {
            throw new IllegalArgumentException(
                "State of charge must be between 0 and 100, got: " + socPercent
            );
        }
    }

    /**
     * Validate that battery capacity is positive.
     * 
     * @param batteryCapacityKwh Battery capacity in kilowatt-hours
     * @throws IllegalArgumentException if capacity is not positive
     */
    private static void validateBatteryCapacity(double batteryCapacityKwh) {
        if (batteryCapacityKwh <= 0) {
            throw new IllegalArgumentException(
                "Battery capacity must be positive, got: " + batteryCapacityKwh
            );
        }
    }
}
