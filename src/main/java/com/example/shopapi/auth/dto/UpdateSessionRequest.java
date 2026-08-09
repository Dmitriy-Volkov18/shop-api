package com.example.shopapi.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(description = "Update session information request")
public record UpdateSessionRequest (

    @Size(max = 100)
    @Schema(
            description = "Optional session nickname",
            example = "My laptop"
    )
    String nickname
){}