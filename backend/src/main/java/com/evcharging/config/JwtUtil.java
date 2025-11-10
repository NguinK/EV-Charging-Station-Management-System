package com.evcharging.config;

import com.evcharging.enums.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtUtil {

    // Tạo key từ chuỗi bí mật
    private final Key SECRET_KEY = Keys.hmacShaKeyFor(
            "u8G0z7h1Qm9Zt6yJp3X4n8a2d9F5r6c7d8e9f0g1h2i3j4k5l6m7n8o9p0q1r2s".getBytes()
    );
    private final long EXPIRATION_TIME = 86_400_000;

    public String generateToken(String email, Role role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role.name())   // thêm role vào claim
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        Claims claims = Jwts.parser()              // trả về JwtParserBuilder
                .setSigningKey(SECRET_KEY)        // truyền key
                .build()                          // build parser
                .parseClaimsJws(token)            // parse token
                .getBody();                       // lấy claims
        return claims.getSubject();
    }

    public String extractRole(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("role", String.class);
    }

    public Instant getExpirationFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getExpiration().toInstant();
    }

    private boolean isTokenExpired(String token) {
        return getExpirationFromToken(token).isBefore(Instant.now());
    }


    public boolean validateToken(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername());
    }
}
