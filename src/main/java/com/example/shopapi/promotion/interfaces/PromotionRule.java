package com.example.shopapi.promotion.interfaces;

import com.example.shopapi.promotion.entities.Promotion;
import com.example.shopapi.promotion.engine.PromotionContext;
import com.example.shopapi.promotion.enums.PromotionRuleType;

public interface PromotionRule {

    PromotionRuleType getType();

    boolean matches(
            Promotion promotion,
            PromotionContext context
    );

}