package com.fundoonotes.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class TokenUtil {

    @Value("${jwt.secret.key}")
    private String secretKey;

    @Value("${jwt.expiration.ms}")
    private long expirationMs;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String generateToken(Long userId) {
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    public Long getUserIdFromToken(String token) {
        String cleanToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(cleanToken)
                .getPayload();
        return Long.parseLong(claims.getSubject());
    }

    public boolean validateToken(String token) {
        try {
            String cleanToken = token.startsWith("Bearer ") ? token.substring(7) : token;
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(cleanToken);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}