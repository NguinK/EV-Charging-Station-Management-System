package com.evcharging.dto.admin;

import com.evcharging.enums.AccountStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffResponse {
    private Long id;
    private String email;
    private String phone;
    private String fullName;
    private AccountStatus status;
    private OffsetDateTime createdAt;
    private List<StaffStationInfo> assignedStations;

    // Only returned when first creating staff
    private String temporaryPassword;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StaffStationInfo {
        private Long stationId;
        private String stationName;
        private String stationLocation;
        private OffsetDateTime assignedAt;
    }
}
