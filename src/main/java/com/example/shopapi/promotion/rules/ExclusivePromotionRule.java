package com.example.shopapi.promotion.rules;

import com.example.shopapi.promotion.entities.Promotion;
import com.example.shopapi.promotion.interfaces.PromotionStackingRule;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ExclusivePromotionRule
        implements PromotionStackingRule {

    @Override
    public boolean isApplicable(
            Promotion candidate,
            List<Promotion> selected
    ) {
        if(candidate.isExclusive() && !selected.isEmpty()) {
            return false;
        }

        return selected.stream()
                .noneMatch(Promotion::isExclusive);
    }
}