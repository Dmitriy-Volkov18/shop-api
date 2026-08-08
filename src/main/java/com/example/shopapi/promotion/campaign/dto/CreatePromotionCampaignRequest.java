package com.example.shopapi.promotion.campaign.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record CreatePromotionCampaignRequest(

        @NotBlank
        @Size(max = 150)
        String name,

        @Size(max = 500)
        String description,

        @NotNull
        LocalDateTime startsAt,

        @NotNull
        LocalDateTime endsAt

) {
}