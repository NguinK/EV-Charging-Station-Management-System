package com.evcharging.dto;

import com.evcharging.enums.ConnectorType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.List;

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
    private Integer totalPoints;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private OffsetDateTime createdAt;   // thời gian tạo trạm sạc

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private OffsetDateTime updatedAt;   // thời gian cập nhật trạm sạc

    private List<ConnectorType> availableConnectorTypes;
    private Integer availablePoints;
}
