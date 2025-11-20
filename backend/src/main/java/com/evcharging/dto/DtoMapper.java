package com.evcharging.dto;

import com.evcharging.entity.*;
import org.springframework.stereotype.Component;
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
        dto.setCustomerEmail(invoice.getCustomerEmail());
        return dto;
    }

    public TransactionDTO toTransactionDTO(Transaction tx) {
        TransactionDTO dto = new TransactionDTO();
        dto.setId(tx.getId());
        dto.setTransactionTime(tx.getTimestamp()); // đổi tên cho khớp DTO
        dto.setAmount(tx.getAmount().doubleValue());
        dto.setCurrency(tx.getCurrency());

        // Enum -> String, có thể null
        dto.setPaymentType(tx.getType() != null ? tx.getType().name() : null);
        dto.setPaymentMethod(tx.getPaymentMethod() != null ? tx.getPaymentMethod().name() : null);
        dto.setStatus(tx.getStatus() != null ? tx.getStatus().name() : null);
        dto.setInvoiceNumber(tx.getInvoiceNumber());
        dto.setPaidAt(tx.getPaidAt()); //chỉ có sau khi thanh toán

        if (tx.getDriver() != null) {
            dto.setDriverId(tx.getDriver().getId());
            dto.setDriverName(tx.getDriver().getFullName());
        }

        if (tx.getSession() != null) {
            dto.setSessionId(tx.getSession().getId());
            dto.setDescription("Thanh toán phiên sạc tại trạm "
                    + tx.getSession().getStation().getName());
        } else {
            dto.setDescription("Thanh toán phiên sạc");
        }
        return dto;
    }

    //ChargingPoint mapping methods
    public ChargingPointResponseDTO toChargingPointDTO(ChargingPoint point) {
        if (point == null) {
            return null;
        }

        ChargingPointResponseDTO.ChargingPointResponseDTOBuilder builder = ChargingPointResponseDTO.builder()
                .id(point.getId())
                .pointCode(point.getPointCode())
                .connectorType(point.getConnectorType())
                .maxPower(point.getMaxPower())
                .speed(point.getSpeed())
                .status(point.getStatus())
                .pricePerKwh(point.getPricePerKwh())
                .pricePerMinute(point.getPricePerMinute())
                .createdAt(point.getCreatedAt())
                .updatedAt(point.getUpdatedAt());

        if (point.getStation() != null) {
            builder.stationId(point.getStation().getId())
                    .stationName(point.getStation().getName())
                    .location(point.getStation().getLocation());
        }

        return builder.build();
    }

    public List<ChargingPointResponseDTO> toChargingPointDTOList(List<ChargingPoint> points) {
        if (points == null || points.isEmpty()) {
            return List.of();
        }

        return points.stream()
                .map(this::toChargingPointDTO)
                .collect(Collectors.toList());
    }

    //ChargingStation mapping methods
    public ChargingStationResponseDTO toChargingStationDTO(ChargingStation station) {
        if (station == null) {
            return null;
        }

        return ChargingStationResponseDTO.builder()
                .id(station.getId())
                .name(station.getName())
                .location(station.getLocation())
                .status(station.getStatus().name())
                .totalPoints(station.getChargingPoints() != null ? station.getChargingPoints().size() : 0)
                .createdAt(station.getCreatedAt())
                .updatedAt(station.getUpdatedAt())
                .build();
    }

    // Wallet mapping methods
    public WalletResponseDTO toWalletDTO(Wallet wallet) {
        if (wallet == null) {
            return null;
        }

        WalletResponseDTO.WalletResponseDTOBuilder builder = WalletResponseDTO.builder()
                .id(wallet.getId())
                .balance(wallet.getBalance())
                .status(wallet.getStatus().name())
                .createdAt(wallet.getCreatedAt())
                .updatedAt(wallet.getUpdatedAt());

        if (wallet.getAccount() != null) {
            builder.accountId(wallet.getAccount().getId());
        }

        return builder.build();
    }

    public WalletTransactionResponseDTO toWalletTransactionDTO(WalletTransaction tx) {
        if (tx == null) {
            return null;
        }

        WalletTransactionResponseDTO.WalletTransactionResponseDTOBuilder builder = WalletTransactionResponseDTO.builder()
                .id(tx.getId())
                .type(tx.getType().name())
                .amount(tx.getAmount())
                .balanceBefore(tx.getBalanceBefore())
                .balanceAfter(tx.getBalanceAfter())
                .description(tx.getDescription())
                .createdAt(tx.getCreatedAt());

        if (tx.getWallet() != null) {
            builder.walletId(tx.getWallet().getId());
        }

        return builder.build();
    }
}
