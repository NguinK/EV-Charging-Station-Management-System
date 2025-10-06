package com.evcharging.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO phản hồi khi trả thông tin trạm sạc về cho client.
 * Dùng trong các API: GET /stations, GET /stations/{id}, v.v.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChargingStationResponseDTO {

    private Long id;
    private String name;
    private String location;
    private String status;     // ONLINE / OFFLINE / MAINTENANCE
    private Integer totalPorts;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime createdAt;   // thời gian tạo trạm sạc

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime updatedAt;   // thời gian cập nhật trạm sạc
}
