package com.evcharging.service;

import com.evcharging.dto.ChargingStationCreateDTO;
import com.evcharging.dto.ChargingStationResponseDTO;
import com.evcharging.dto.DtoMapper;
import com.evcharging.entity.ChargingStation;
import com.evcharging.enums.ConnectorType;
import com.evcharging.enums.StationStatus;
import com.evcharging.repository.ChargingStationRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ChargingStationService {

    private final ChargingStationRepository stationRepo;
    private final DtoMapper dtoMapper;

    public ChargingStationService(ChargingStationRepository stationRepo,
                                  DtoMapper dtoMapper) {
        this.stationRepo = stationRepo;
        this.dtoMapper = dtoMapper;
    }

    // Lấy tất cả trạm
    public List<ChargingStation> getAllStations() {
        return stationRepo.findAll();
    }

    // Lọc theo tình trạng (AVAILABLE, OFFLINE)
    public List<ChargingStation> getStationsByStatus(StationStatus status) {
        return stationRepo.findByStatus(status);
    }

    // Lọc theo loại cổng sạc
    public List<ChargingStation> getStationsByConnectorType(ConnectorType type) {
        return stationRepo.findByConnectorType(type);
    }

    // Lọc theo bán kính vị trí (giả sử có lat/lng trong entity)
    public List<ChargingStation> getStationsNearby(double lat, double lng, double radiusKm) {
        return stationRepo.findAll().stream()
                .filter(s -> distanceKm(lat, lng, s.getLatitude(), s.getLongitude()) <= radiusKm)
                .toList();
    }

    public ChargingStationResponseDTO createStation(ChargingStationCreateDTO dto) {
        ChargingStation station = new ChargingStation();
        station.setName(dto.getName());
        station.setLocation(dto.getLocation());
        station.setStatus(dto.getStatus());
        station.setTotalPoints(dto.getTotalPoints());
        station.setOperator("EVN"); // hoặc lấy từ context nếu cần
        station.setLatitude(0.0);   // có thể cập nhật sau
        station.setLongitude(0.0);  // có thể cập nhật sau

        ChargingStation saved = stationRepo.save(station);
        return dtoMapper.toChargingStationDTO(saved);
    }

    private double distanceKm(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371; // bán kính Trái Đất km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}