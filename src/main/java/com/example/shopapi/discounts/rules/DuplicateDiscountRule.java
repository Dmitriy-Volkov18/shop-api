package com.example.shopapi.discounts.rules;

import com.example.shopapi.discounts.dto.DiscountResult;
import com.example.shopapi.discounts.interfaces.DiscountStackingRule;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DuplicateDiscountRule
        implements DiscountStackingRule {

    @Override
    public boolean isApplicable(
            DiscountResult candidate,
            List<DiscountResult> selected
    ) {
        return selected.stream()
                .noneMatch(discount ->
                        discount.discount()
                                .getId()
                                .equals(
                                        candidate.discount().getId()
                                )
                );
    }
}