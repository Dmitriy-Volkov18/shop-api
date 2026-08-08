package com.example.shopapi.promotion.rules;

import com.example.shopapi.promotion.entities.Promotion;
import com.example.shopapi.promotion.engine.PromotionContext;
import com.example.shopapi.promotion.enums.PromotionRuleType;
import com.example.shopapi.promotion.interfaces.PromotionRule;
import com.example.shopapi.promotion.services.UserPromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FirstOrderPromotionRule
        implements PromotionRule {

    private final UserPromotionService service;

    @Override
    public PromotionRuleType getType() {
        return PromotionRuleType.FIRST_ORDER;
    }

    @Override
    public boolean matches(
            Promotion promotion,
            PromotionContext context
    ) {
        if(context.getUser() == null){
            return false;
        }

        return service.isFirstOrder(
                context.getUser()
        );
    }
}