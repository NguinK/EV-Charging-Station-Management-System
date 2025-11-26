package com.evcharging.service;

import com.evcharging.dto.ReservationCreateDTO;
import com.evcharging.dto.ReservationResponseDTO;
import com.evcharging.entity.ChargingPoint;
import com.evcharging.entity.ChargingStation;
import com.evcharging.entity.EVDriver;
import com.evcharging.entity.Reservation;
import com.evcharging.enums.ChargingPointStatus;
import com.evcharging.enums.ReservationStatus;
import com.evcharging.repository.ChargingPointRepository;
import com.evcharging.repository.ChargingStationRepository;
import com.evcharging.repository.EVDriverRepository;
import com.evcharging.repository.ReservationRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;


@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final EVDriverRepository driverRepository;
    private final ChargingStationRepository stationRepository;
    private final ChargingPointRepository chargingPointRepository;
    private final PricingService pricingService;

    public ReservationService(ReservationRepository reservationRepository,
                              EVDriverRepository driverRepository,
                              ChargingStationRepository stationRepository,
                              ChargingPointRepository chargingPointRepository,
                              PricingService pricingService) {
        this.reservationRepository = reservationRepository;
        this.driverRepository = driverRepository;
        this.stationRepository = stationRepository;
        this.chargingPointRepository = chargingPointRepository;
        this.pricingService = pricingService;
    }

    // Hàm dùng chung để map Entity -> DTO
    private ReservationResponseDTO mapToDTO(Reservation res) {
        BigDecimal holdFee = pricingService.calculateReservationHoldFee(res);
        return new ReservationResponseDTO(
                res.getId(),
                res.getStation().getName(),
                res.getConnectorType(),
                res.getStatus(),
                res.getStartTime(),
                res.getExpireTime(),
                res.getChargingPoint().getId(),
                res.getStation().getId(),
                holdFee.doubleValue()

        );
    }

    @Transactional
    public ReservationResponseDTO createReservationAutoApprove(Long driverId, ReservationCreateDTO dto) {
        EVDriver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        ChargingStation station = stationRepository.findById(dto.getStationId())
                .orElseThrow(() -> new RuntimeException("Station not found"));
        // 3. check connector type
        if (dto.getConnectorType() == null) {
            throw new IllegalArgumentException("Connector type is required");
        }

        // Lấy giờ hiện tại theo UTC
        OffsetDateTime nowUTC = OffsetDateTime.now(ZoneOffset.UTC);
        OffsetDateTime endTime = dto.getEndTime();

        if (endTime == null) {
            throw new RuntimeException("Vui lòng nhập thời điểm bạn dự kiến đến trạm sạc");
        }

        if (endTime.isBefore(nowUTC)) {
            throw new RuntimeException("End time must be in the future (UTC)");
        }

        // Lấy tất cả trụ trong trạm
        List<ChargingPoint> stationPoints = chargingPointRepository.findByStationId(dto.getStationId());
        List<ChargingPoint> filteredPoints = stationPoints.stream()
                .filter(p -> p.getConnectorType().equals(dto.getConnectorType()))
                .filter(p -> p.getStatus() == ChargingPointStatus.AVAILABLE)
                .toList();

        for (ChargingPoint point : filteredPoints) {
            boolean isOccupied = reservationRepository.existsByChargingPointAndStatusInAndTimeOverlap(
                    point,
                    List.of(ReservationStatus.CONFIRMED),
                    nowUTC,
                    endTime
            );

            if (!isOccupied) {
                Reservation reservation = new Reservation();
                reservation.setDriver(driver);
                reservation.setChargingPoint(point);
                reservation.setStation(station);
                reservation.setConnectorType(dto.getConnectorType());
                reservation.setStartTime(nowUTC); // thời điểm đặt
                reservation.setExpireTime(endTime); // thời điểm driver dự kiến đến
                reservation.setStatus(ReservationStatus.CONFIRMED);

                point.setStatus(ChargingPointStatus.RESERVED);
                chargingPointRepository.save(point);

                Reservation saved = reservationRepository.save(reservation);
                return mapToDTO(saved);
            }
        }

        throw new RuntimeException("Không còn trụ sạc nào trống tại thời điểm này");
    }

    public ReservationResponseDTO getReservationDetails(Long reservationId) {
        Reservation res = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
        return mapToDTO(res);
    }

    @Scheduled(fixedRate = 5 * 60 * 1000) // mỗi 5 phút
    public void cancelExpiredReservations() {
        OffsetDateTime nowUTC = OffsetDateTime.now(ZoneOffset.UTC);

        List<Reservation> expired = reservationRepository.findByStatusAndExpireTimeBefore(
                ReservationStatus.CONFIRMED, nowUTC
        );

        for (Reservation r : expired) {
            r.setStatus(ReservationStatus.CANCELLED);

            ChargingPoint point = r.getChargingPoint();
            point.setStatus(ChargingPointStatus.AVAILABLE);

            chargingPointRepository.save(point);
            reservationRepository.save(r);
        }
    }

    @Transactional
    public void cancelReservation(Long id) {
        Reservation res = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
        res.setStatus(ReservationStatus.CANCELLED);

        ChargingPoint point = res.getChargingPoint();
        point.setStatus(ChargingPointStatus.AVAILABLE);
        chargingPointRepository.save(point);

        reservationRepository.save(res);
    }

    public List<ReservationResponseDTO> getReservationList(Long accountId) {
        EVDriver driver = driverRepository.findByAccountId(accountId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        List<Reservation> reservations = reservationRepository.findByDriver(driver);
        return reservations.stream()
                .map(this::mapToDTO)
                .toList();
    }
}

//    @Transactional tạo thủ công
//    public ReservationResponseDTO createReservation(Long driverId, ReservationCreateDTO dto) {
//        EVDriver driver = driverRepository.findById(driverId)
//                .orElseThrow(() -> new RuntimeException("Driver not found"));
//        ChargingStation station = stationRepository.findById(dto.getStationId())
//                .orElseThrow(() -> new RuntimeException("Station not found"));
//
//        Reservation reservation = new Reservation();
//        reservation.setDriver(driver);
//        reservation.setStation(station);
//        reservation.setConnectorType(dto.getConnectorType());
//        reservation.setStatus(ReservationStatus.CONFIRMED);
//        reservation.setStartTime(dto.getStartTime());
//        reservation.setExpireTime(dto.getStartTime().plusMinutes(30));
//
//        Reservation saved = reservationRepository.save(reservation);
//
//        return new ReservationResponseDTO(
//                saved.getId(),
//                station.getName(),
//                saved.getConnectorType(),
//                saved.getStatus(),
//                saved.getStartTime(),
//                saved.getExpireTime()
//        );
//    }

