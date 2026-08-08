package com.example.shopapi.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Refresh token request")
public class RefreshRequest {

    @NotBlank
    @Schema(
            description = "Refresh token",
            example = "eyJhbGciOiJIUzI1NiJ9..."
    )
    private String refreshToken;
}