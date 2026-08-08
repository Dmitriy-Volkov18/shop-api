package com.example.shopapi.auth.dto;

import java.time.Instant;

public record SessionResponse(
        String jti,

        String deviceId,

        String nickname,

        String deviceName,
        String deviceType,

        String browser,
        String browserVersion,

        String operatingSystem,
        String operatingSystemVersion,

        String country,
        String ipAddress,

        Instant createdAt,
        Instant lastUsedAt,
        Instant expiryDate,

        boolean current,
        boolean active,
        boolean trusted
) {}