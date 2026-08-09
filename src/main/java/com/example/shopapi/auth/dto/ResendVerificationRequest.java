package com.example.shopapi.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Resend email verification request")
public record ResendVerificationRequest (

    @NotBlank
    @Email
    @Schema(
            description = "Email associated with the account",
            example = "john@example.com"
    )
    String email
){}