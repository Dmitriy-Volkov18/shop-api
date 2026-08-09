package com.example.shopapi.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "User login request")
public record LoginRequest (

    @NotBlank
    @Schema(
            description = "Username",
            example = "john"
    )
    String username,

    @NotBlank
    @Schema(
            description = "User password",
            example = "StrongPassword123!"
    )
    String password
){

    public LoginRequest withPassword(String newPassword) {
        return new LoginRequest(this.username, newPassword);
    }
}
