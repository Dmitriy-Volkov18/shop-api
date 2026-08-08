package com.example.shopapi.promotion.targets;

import com.example.shopapi.promotion.engine.PromotionContext;
import com.example.shopapi.promotion.entities.Promotion;
import com.example.shopapi.promotion.enums.PromotionTargetType;
import com.example.shopapi.promotion.interfaces.PromotionTarget;
import org.springframework.stereotype.Component;

@Component
public class AllPromotionTarget
        implements PromotionTarget {

    @Override
    public PromotionTargetType getType() {

        return PromotionTargetType.ALL;
    }

    @Override
    public boolean matches(
            Promotion promotion,
            PromotionContext context
    ) {

        return true;
    }
}