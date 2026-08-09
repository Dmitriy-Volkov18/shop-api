package com.example.shopapi.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Authentication response")
public record AuthResponse(

        @Schema(
                description = "JWT access token",
                example = "eyJhbGciOiJIUzI1NiJ9...",
                accessMode = Schema.AccessMode.READ_ONLY
        )
        String accessToken,

        @Schema(
                description = "Refresh token",
                example = "eyJhbGciOiJIUzI1NiJ9...",
                accessMode = Schema.AccessMode.READ_ONLY
        )
        String refreshToken,

        @Schema(
                description = "Unique identifier of the current device",
                example = "3f4d7c3e-6d1e-4a1a-9d8e-123456789abc"
        )
        String deviceId
) {}
