package com.example.shopapi.discounts.rules;

import com.example.shopapi.common.config.DiscountProperties;
import com.example.shopapi.discounts.dto.DiscountResult;
import com.example.shopapi.discounts.interfaces.DiscountStackingRule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MaxDiscountRule
        implements DiscountStackingRule {

    private final DiscountProperties properties;

    @Override
    public boolean isApplicable(
            DiscountResult candidate,
            List<DiscountResult> selected
    ) {
        BigDecimal total =
                selected.stream()
                        .filter(DiscountResult::isPercent)
                        .map(DiscountResult::discountValue)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        if(candidate.isPercent()){
            total = total.add(
                    candidate.discountValue()
            );
        }

        return total.compareTo(properties.getMaxTotalPercent()) <= 0;
    }
}