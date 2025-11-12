package com.evcharging.dto.admin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PeakHourData {
    private Integer hour;  // 0-23
    private Integer count;
}
