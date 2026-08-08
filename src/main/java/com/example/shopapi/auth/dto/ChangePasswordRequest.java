package com.example.shopapi.auth.dto;

import com.example.shopapi.common.validation.StrongPassword;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Change password request")
public class ChangePasswordRequest {

        @NotBlank
        @Schema(
                description = "Current password",
                example = "OldPassword123!"
        )
        private String oldPassword;

        @StrongPassword
        @Schema(
                description = "New password",
                example = "NewStrongPassword123!"
        )
        private String newPassword;
}