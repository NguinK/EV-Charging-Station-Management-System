package com.evcharging.service.staff;

public interface StaffChargingPointService {
    //Set charger status to AVAILABLE
    void setChargingPointAvailable(Long chargerId);

    //Set charger status to OUT_OF_SERVICE
    void setChargerOutOfService(Long chargerId, String reason);
}
