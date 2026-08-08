package com.example.shopapi.discounts.rules;

import com.example.shopapi.discounts.dto.DiscountResult;
import com.example.shopapi.discounts.interfaces.DiscountStackingRule;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ExclusiveDiscountRule
        implements DiscountStackingRule {

    @Override
    public boolean isApplicable(
            DiscountResult candidate,
            List<DiscountResult> selected
    ) {
        if(candidate.isExclusive() && !selected.isEmpty()) {
            return false;
        }

        return selected.stream()
                .noneMatch(
                        DiscountResult::isExclusive
                );
    }
}