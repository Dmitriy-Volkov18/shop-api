package com.example.shopapi.discounts.rules;

import com.example.shopapi.discounts.dto.DiscountResult;
import com.example.shopapi.discounts.interfaces.DiscountStackingRule;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SameSourceStackingRule
        implements DiscountStackingRule {

    @Override
    public boolean isApplicable(
            DiscountResult candidate,
            List<DiscountResult> selected
    ) {
        return selected.stream()
                .filter(discount -> discount.source() == candidate.source())
                .allMatch(
                        discount ->
                                discount.isStackable()
                                        &&
                                        candidate.isStackable()
                );
    }
}