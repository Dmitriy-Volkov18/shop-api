package com.example.shopapi.auth.dto;

import com.example.shopapi.common.validation.StrongPassword;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "User registration request")
public record RegisterRequest (

    @NotBlank
    @Size(min = 3, max = 50)
    @Schema(
            description = "Unique username",
            example = "john"
    )
    String username,

    @NotBlank
    @Email
    @Schema(
            description = "User email",
            example = "john@example.com"
    )
    String email,

    @StrongPassword
    @Schema(
            description = "User password",
            example = "StrongPassword123!"
    )
    String password
){
    public RegisterRequest withUsername(String newUsername) {
        return new RegisterRequest(newUsername, this.email, this.password);
    }
    public RegisterRequest withEmail(String newEmail) {
        return new RegisterRequest(this.username, newEmail, this.password);
    }
}