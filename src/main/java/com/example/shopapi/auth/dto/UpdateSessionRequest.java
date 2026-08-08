package com.example.shopapi.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Update session information request")
public class UpdateSessionRequest {

    @Size(max = 100)
    @Schema(
            description = "Optional session nickname",
            example = "My laptop"
    )
    private String nickname;
}