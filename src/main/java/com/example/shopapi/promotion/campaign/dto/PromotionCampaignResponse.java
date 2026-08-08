package com.example.shopapi.promotion.campaign.dto;

import com.example.shopapi.promotion.enums.PromotionStatus;

import java.time.LocalDateTime;

public record PromotionCampaignResponse(

        Long id,

        String name,

        String description,

        PromotionStatus status,

        LocalDateTime startsAt,

        LocalDateTime endsAt,

        Integer promotionCount

) {
}