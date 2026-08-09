package com.example.shopapi.promotion.actions;

import com.example.shopapi.promotion.engine.PromotionContext;
import com.example.shopapi.promotion.engine.PromotionResult;
import com.example.shopapi.promotion.entities.Promotion;
import com.example.shopapi.promotion.enums.PromotionActionType;
import com.example.shopapi.promotion.interfaces.PromotionAction;
import org.springframework.stereotype.Component;


@Component
public class FreeShippingPromotionAction implements PromotionAction {

    @Override
    public PromotionActionType getType() {
        return PromotionActionType.FREE_SHIPPING;
    }

    @Override
    public PromotionResult apply(
            Promotion promotion,
            PromotionContext context
    ) {
        return PromotionResult.withFreeShipping();
    }
}