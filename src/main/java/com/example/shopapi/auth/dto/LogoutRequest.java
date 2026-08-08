package com.example.shopapi.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Logout current session request")
public class LogoutRequest {

    @NotBlank
    @Schema(
            description = "Refresh token of the current session",
            example = "eyJhbGciOiJIUzI1NiJ9..."
    )
    private String refreshToken;
}