package com.evcharging.service.staff;

import com.evcharging.dto.staff.RecordPaymentRequest;
import com.evcharging.dto.staff.TransactionDetailResponse;

public interface StaffPaymentService {
    //Ghi nhận thanh toán bằng tiền mặt/thẻ/ví điện tử
    TransactionDetailResponse recordPayment(Long transactionId, RecordPaymentRequest request);

    //Lấy chi tiết transaction
    TransactionDetailResponse getTransactionDetail(Long transactionId);

    //Lấy danh sách transaction pending của 1 station
    java.util.List<TransactionDetailResponse> getPendingTransactions(Long stationId);

    //Hoàn tiền(refund)
    TransactionDetailResponse refundTransaction(Long transactionId, String reason);
}
