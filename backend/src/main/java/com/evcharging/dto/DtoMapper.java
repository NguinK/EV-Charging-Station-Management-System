package com.evcharging.dto;

import com.evcharging.dto.admin.ChargingPointResponse;
import com.evcharging.entity.Invoice;
import com.evcharging.entity.Transaction;
import org.springframework.stereotype.Component;
import com.evcharging.entity.ChargingPoint;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DtoMapper {
//    public static PaymentDTO toPaymentDTO(Payment payment) {
//        PaymentDTO dto = new PaymentDTO();
//        dto.setId(payment.getId());
//        dto.setSessionId(payment.getSession().getId());
//        dto.setAccountId(payment.getAccount().getId());
//        dto.setAmount(payment.getAmount());
//        dto.setEnergyCost(payment.getEnergyCost());
//        dto.setTimeCost(payment.getTimeCost());
//        dto.setDiscount(payment.getDiscount());
//        dto.setFinalAmount(payment.getFinalAmount());
//        dto.setMethod(payment.getMethod());
//        dto.setStatus(payment.getStatus());
//        dto.setTransactionId(payment.getTransactionId());
//        dto.setPaymentTime(payment.getPaymentTime());
//        dto.setInvoiceNumber(payment.getInvoice().getInvoiceNumber());
//        return dto;
//    }

    public InvoiceDTO toDTO(Invoice invoice) {
        InvoiceDTO dto = new InvoiceDTO();
        dto.setId(invoice.getId());
        dto.setInvoiceNumber(invoice.getInvoiceNumber());
        dto.setCustomerName(invoice.getCustomerName());
        dto.setStationName(invoice.getStationName());
        dto.setPointCode(invoice.getPointCode());
        dto.setFinalAmount(invoice.getFinalAmount());
        return dto;
    }


    public static TransactionDTO toTransactionDTO(Transaction tx) {
        TransactionDTO dto = new TransactionDTO();
        dto.setId(tx.getId());
        dto.setTransactionTime(tx.getTimestamp()); // đổi tên cho khớp DTO
        dto.setAmount(tx.getAmount().doubleValue());
        dto.setCurrency(tx.getCurrency());
        dto.setPaymentType(tx.getType().name()); // Enum -> String
        dto.setPaymentMethod(tx.getPaymentMethod().name()); // Enum -> String
        dto.setStatus(tx.getStatus().name());
        dto.setInvoiceNumber(tx.getInvoiceNumber());
        dto.setPaidAt(tx.getPaidAt());

        if (tx.getDriver() != null) {
            dto.setDriverId(tx.getDriver().getId());
            dto.setDriverName(tx.getDriver().getFullName());
        }

        if (tx.getSession() != null) {
            dto.setSessionId(tx.getSession().getId());
        }
        dto.setDescription("Thanh toán phiên sạc tại trạm "
                + (tx.getSession() != null ? tx.getSession().getStation().getName() : ""));
        return dto;
    }
    /**
     * Convert ChargingPoint entity to ChargingPointResponseDTO
     */
    public ChargingPointResponse toChargingPointDTO(ChargingPoint point) {
        if (point == null) {
            return null;
        }

        return ChargingPointResponse.builder()
                .id(point.getId())
                .pointCode(point.getPointCode())
                .connectorType(point.getConnectorType())
                .maxPower(point.getMaxPower())
                .speed(point.getSpeed())
                .status(point.getStatus())
                .pricePerKwh(point.getPricePerKwh())
                .pricePerMinute(point.getPricePerMinute())
                .createdAt(point.getCreatedAt())
                .updatedAt(point.getUpdatedAt())
                .stationId(point.getStation().getId())
                .stationName(point.getStation().getName())
                .location(point.getStation().getLocation())
                .build();
    }
    public List<ChargingPointResponse> toChargingPointDTOList(List<ChargingPoint> points) {
        if (points == null) {
            return List.of();
        }

        return points.stream()
                .map(this::toChargingPointDTO)
                .collect(Collectors.toList());
    }
}
