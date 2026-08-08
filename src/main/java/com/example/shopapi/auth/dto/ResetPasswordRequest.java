package com.example.shopapi.auth.dto;

import com.example.shopapi.common.validation.StrongPassword;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Reset password request")
public class ResetPasswordRequest {

    @NotBlank
    @Schema(
            description = "Password reset token",
            example = "550e8400-e29b-41d4-a716-446655440000"
    )
    private String token;

    @StrongPassword
    @Schema(
            description = "New password",
            example = "NewStrongPassword123!"
    )
    private String newPassword;
}