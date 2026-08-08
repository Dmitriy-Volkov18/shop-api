package com.example.shopapi.auth.dto;

import com.example.shopapi.common.validation.StrongPassword;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "User registration request")
public class RegisterRequest {

    @NotBlank
    @Size(min = 3, max = 50)
    @Schema(
            description = "Unique username",
            example = "john"
    )
    private String username;

    @NotBlank
    @Email
    @Schema(
            description = "User email",
            example = "john@example.com"
    )
    private String email;

    @StrongPassword
    @Schema(
            description = "User password",
            example = "StrongPassword123!"
    )
    private String password;
}