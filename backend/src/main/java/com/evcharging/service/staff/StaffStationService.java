package com.evcharging.service.staff;

import com.evcharging.dto.staff.StationSummaryResponse;

import java.util.List;

public interface StaffStationService {
    //Get all stations assigned to the current staff member
    List<StationSummaryResponse> getMyStations();

    //Verify if current staff has access to a specific station
    boolean hasAccessToStation(Long stationId);

    //Get list of station IDs accessible by current staff
    List<Long> getAccessibleStationIds();

}
