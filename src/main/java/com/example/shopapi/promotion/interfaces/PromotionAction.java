package com.example.shopapi.promotion.interfaces;

import com.example.shopapi.promotion.entities.Promotion;
import com.example.shopapi.promotion.engine.PromotionContext;
import com.example.shopapi.promotion.engine.PromotionResult;
import com.example.shopapi.promotion.enums.PromotionActionType;

import java.math.BigDecimal;

public interface PromotionAction {

    PromotionActionType getType();

    PromotionResult apply(
            Promotion promotion,
            PromotionContext context
    );

}