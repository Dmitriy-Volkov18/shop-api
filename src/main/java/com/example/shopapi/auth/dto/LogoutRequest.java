package com.example.shopapi.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Logout current session request")
public record LogoutRequest (

    @NotBlank
    @Schema(
            description = "Refresh token of the current session",
            example = "eyJhbGciOiJIUzI1NiJ9..."
    )
    String refreshToken
){}