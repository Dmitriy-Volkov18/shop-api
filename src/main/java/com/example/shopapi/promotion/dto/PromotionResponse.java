package com.example.shopapi.promotion.dto;

import com.example.shopapi.promotion.enums.PromotionStatus;

import java.time.LocalDateTime;

public record PromotionResponse(

        Long id,

        String name,

        String description,

        PromotionStatus status,

        LocalDateTime startsAt,

        LocalDateTime endsAt,

        Integer priority

) {
}