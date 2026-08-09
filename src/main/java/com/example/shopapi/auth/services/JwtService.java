package com.example.shopapi.auth.services;

import com.example.shopapi.auth.dto.RefreshTokenClaims;
import com.example.shopapi.common.exception.runtimeExceptions.BadRequestException;
import com.example.shopapi.user.entities.User;
import com.example.shopapi.common.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties jwtProperties;

    private SecretKey getSignKey() {
        return Keys.hmacShaKeyFor(
                jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8)
        );
    }

    public String generateAccessToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().name());
        claims.put("userId", user.getId());
        claims.put("jti", UUID.randomUUID().toString());
        claims.put("tokenVersion", user.getTokenVersion());

        return Jwts.builder()
                .claims(claims)
                .subject(user.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() +  jwtProperties.getAccessExpiration().toMillis()))
                .signWith(getSignKey())
                .compact();
    }

    public String generateRefreshToken(User user, String deviceId, String familyId) {
        String jti = UUID.randomUUID().toString();

        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("deviceId", deviceId)
                .claim("jti", jti)
                .claim("familyId", familyId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() +  jwtProperties.getRefreshExpiration().toMillis()))
                .signWith(getSignKey())
                .compact();
    }

    public String extractUsername(String token) {
        return parseClaims(token)
                .getSubject();
    }

    public String extractRole(String token) {
        return parseClaims(token).get("role", String.class);
    }

    public Long extractUserId(String token) {

        return parseClaims(token)
                .get("userId", Long.class);
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractJti(String token) {
        return parseClaims(token)
                .get("jti", String.class);
    }

    public String extractDeviceId(String token) {
        return parseClaims(token)
                .get("deviceId", String.class);
    }

    public String extractFamilyId(String token) {
        return parseClaims(token)
                .get("familyId", String.class);
    }

    public RefreshTokenClaims extractRefreshClaims(String token) {
        Claims claims = parseClaims(token);

        return new RefreshTokenClaims(
                claims.get("jti", String.class),
                claims.get("deviceId", String.class),
                claims.get("familyId", String.class)
        );
    }

    public Long extractTokenVersion(String token) {
        return parseClaims(token).get("tokenVersion", Long.class);
    }

    public String extractAccessToken(
            String authorizationHeader
    ) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new BadRequestException("Missing access token");
        }

        return authorizationHeader.substring(7);
    }
}