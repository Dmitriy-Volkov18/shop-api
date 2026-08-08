package com.example.shopapi.promotion.engine;

import com.example.shopapi.promotion.entities.Promotion;

public record AppliedPromotion(
        Promotion promotion,
        PromotionResult result
) {
}