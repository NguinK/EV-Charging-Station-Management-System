package com.evcharging.service;

import com.evcharging.entity.EVDriver;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final EmailService emailService;

    public NotificationService(EmailService emailService) {
        this.emailService = emailService;
    }

    public void sendChargingComplete(EVDriver driver) {
        String email = driver.getAccount().getEmail();
        String subject = " Phiên sạc đã hoàn tất";
        String message = "Xe của bạn đã sạc đầy 100%. Vui lòng rút sạc để nhường cho người khác.";

        emailService.send(email, subject, message);
    }
}