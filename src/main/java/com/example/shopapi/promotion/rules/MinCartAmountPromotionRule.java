package com.example.shopapi.promotion.rules;

import com.example.shopapi.promotion.engine.PromotionContext;
import com.example.shopapi.promotion.entities.Promotion;
import com.example.shopapi.promotion.enums.PromotionRuleType;
import com.example.shopapi.promotion.interfaces.PromotionRule;
import org.springframework.stereotype.Component;


@Component
public class MinCartAmountPromotionRule implements PromotionRule {

    @Override
    public PromotionRuleType getType() {
        return PromotionRuleType.MIN_CART_AMOUNT;
    }

    @Override
    public boolean matches(
            Promotion promotion,
            PromotionContext context
    ) {
        if(context.getCartTotal() == null) {
            return false;
        }

        return context.getCartTotal().compareTo(promotion.getRuleValue()) >= 0;
    }
}