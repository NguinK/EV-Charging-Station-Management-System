package com.evcharging.dto.admin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateChargingStationRequest {
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
    private String operatorName;
    private String contactPhone;
}
