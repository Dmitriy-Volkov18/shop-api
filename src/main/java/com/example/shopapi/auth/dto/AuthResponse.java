package com.example.shopapi.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "Authentication response")
public class AuthResponse {

    @Schema(
            description = "JWT access token",
            example = "eyJhbGciOiJIUzI1NiJ9...",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private String accessToken;

    @Schema(
            description = "Refresh token",
            example = "eyJhbGciOiJIUzI1NiJ9...",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private String refreshToken;

    @Schema(
            description = "Unique identifier of the current device",
            example = "3f4d7c3e-6d1e-4a1a-9d8e-123456789abc"
    )
    private String deviceId;
}