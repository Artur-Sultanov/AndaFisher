package com.example.anda_fisher.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    private final String jwtSecret;
    private final long expirationTimeMs;
    private SecretKey cachedSecretKey;

    public JwtService(@Value("${jwt.secret}") String jwtSecret,
                      @Value("${jwt.expirationMs}") long expirationTimeMs) {
        this.jwtSecret = jwtSecret;
        this.expirationTimeMs = expirationTimeMs;
        validateSecretKey();    }


    @PostConstruct
    private void validateSecretKey() {
        if (jwtSecret == null || jwtSecret.getBytes().length < 32) {
            throw new IllegalArgumentException("JWT secret must be at least 256 bits long (32 characters).");
        }
    }

    private SecretKey getSecretKey() {
        if (cachedSecretKey == null) {
            try {
                cachedSecretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Failed to create secret key: " + e.getMessage(), e);
            }
        }
        return cachedSecretKey;
    }

    public String generateToken(String username, String role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("role", "ROLE_" + role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTimeMs))
                .signWith(getSecretKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSecretKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public String extractRole(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("role", String.class);
    }
}