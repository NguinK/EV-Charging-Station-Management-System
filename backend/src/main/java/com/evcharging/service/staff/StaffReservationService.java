package com.evcharging.service.staff;

import com.evcharging.dto.staff.CheckInReservationRequest;
import com.evcharging.dto.staff.ReservationSummaryResponse;

import java.util.List;

public interface StaffReservationService {
    //Get today's reservations for a specific station
    List<ReservationSummaryResponse> getTodayReservations(Long stationId);

    //Check in a a reservation when driver arrives
    ReservationSummaryResponse checkInReservation(Long reservationId, CheckInReservationRequest request);

    //Mark a reservation as no-show and create penalty transaction
    ReservationSummaryResponse markAsNoShow(Long reservationId);
}
