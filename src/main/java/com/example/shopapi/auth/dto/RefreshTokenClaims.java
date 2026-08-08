package com.example.shopapi.auth.dto;

public record RefreshTokenClaims(
        String jti,
        String deviceId,
        String familyId
) {}