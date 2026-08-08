package com.example.shopapi.promotion.interfaces;

import com.example.shopapi.promotion.engine.PromotionContext;
import com.example.shopapi.promotion.entities.Promotion;
import com.example.shopapi.promotion.enums.PromotionTargetType;

public interface PromotionTarget {

    PromotionTargetType getType();

    boolean matches(
            Promotion promotion,
            PromotionContext context
    );
}