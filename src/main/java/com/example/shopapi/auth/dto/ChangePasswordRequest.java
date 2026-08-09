package com.example.shopapi.auth.dto;

import com.example.shopapi.common.validation.StrongPassword;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Change password request")
public record ChangePasswordRequest(

        @NotBlank
        @Schema(description = "Current password", example = "OldPassword123!")
        String oldPassword,

        @StrongPassword
        @Schema(description = "New password", example = "NewStrongPassword123!")
        String newPassword
) {}