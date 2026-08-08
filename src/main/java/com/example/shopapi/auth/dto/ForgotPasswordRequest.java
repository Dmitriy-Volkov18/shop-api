package com.example.shopapi.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Forgot password request")
public class ForgotPasswordRequest {

    @NotBlank
    @Email
    @Schema(
            description = "Email associated with the account",
            example = "john@example.com"
    )
    private String email;
}