package com.example.shopapi.promotion.resolvers;

import com.example.shopapi.promotion.entities.Promotion;
import com.example.shopapi.promotion.engine.PromotionContext;
import com.example.shopapi.promotion.interfaces.PromotionRule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PromotionRuleResolver {

    private final List<PromotionRule> rules;

    public boolean matches(
            Promotion promotion,
            PromotionContext context
    ) {
        return rules.stream()
                .filter(rule ->
                        rule.getType()
                                ==
                                promotion.getRuleType()
                )
                .allMatch(rule ->
                        rule.matches(
                                promotion,
                                context
                        )
                );
    }
}