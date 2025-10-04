package com.evcharging.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class jwtUtil {

    // Tạo key từ chuỗi bí mật (dùng getBytes)
    private final Key SECRET_KEY = Keys.hmacShaKeyFor(
            "u8G0z7h1Qm9Zt6yJp3X4n8a2d9F5r6c7d8e9f0g1h2i3j4k5l6m7n8o9p0q1r2s".getBytes()
    );

    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 ngày
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256) // ✅ đúng
                .compact();
    }
}

