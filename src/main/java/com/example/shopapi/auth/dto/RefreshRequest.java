package com.example.shopapi.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Refresh token request")
public record RefreshRequest (

    @NotBlank
    @Schema(
            description = "Refresh token",
            example = "eyJhbGciOiJIUzI1NiJ9..."
    )
    String refreshToken
){}