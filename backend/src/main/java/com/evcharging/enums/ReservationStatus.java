package com.evcharging.enums;

public enum ReservationStatus {

    CONFIRMED, //Đã xác nhận đặt chỗ
    CHECKED_IN, //Driver đã tới trạm
    CHARGING, //Đang sạc
    COMPLETED, //Sạc xong
    CANCELLED, //Bị hủy
    NO_SHOW,  //Driver không đến, có thể bị phạt
}