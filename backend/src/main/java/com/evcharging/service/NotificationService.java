package com.evcharging.service;

import com.evcharging.dto.NotificationMessage;
import com.evcharging.entity.EVDriver;
import com.evcharging.entity.Transaction;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    private final SimpMessagingTemplate messagingTemplate;

    public NotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // Thông báo realtime khi sạc xong`
    public void sendChargingComplete(EVDriver driver) {
        NotificationMessage msg = new NotificationMessage(
                "Phiên sạc đã hoàn tất",
                "Xe của bạn đã sạc đầy 100%. Vui lòng rút sạc để nhường cho người khác."
        );
        messagingTemplate.convertAndSend("/topic/driver/" + driver.getId(), msg);
    }

    // Thông báo realtime khi thanh toán thành công
    public void sendPaymentSuccess(EVDriver driver, Transaction tx) {
        NotificationMessage msg = new NotificationMessage(
                "Thanh toán thành công",
                "Bạn đã thanh toán " + tx.getAmount() + " VND cho phiên #" + tx.getSession().getId()
        );
        messagingTemplate.convertAndSend("/topic/driver/" + driver.getId(), msg);
    }

}