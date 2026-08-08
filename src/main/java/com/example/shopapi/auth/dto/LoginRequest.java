package com.example.shopapi.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "User login request")
public class LoginRequest {

    @NotBlank
    @Schema(
            description = "Username",
            example = "john"
    )
    private String username;

    @NotBlank
    @Schema(
            description = "User password",
            example = "StrongPassword123!"
    )
    private String password;
}