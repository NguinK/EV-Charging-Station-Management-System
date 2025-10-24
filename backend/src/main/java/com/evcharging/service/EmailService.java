package com.evcharging.service;

import org.springframework.stereotype.Service;

@Service
public class EmailService {

    public void send(String to, String subject, String content) {
        System.out.println("📧 Gửi email tới: " + to);
        System.out.println("Tiêu đề: " + subject);
        System.out.println("Nội dung: " + content);
    }
}