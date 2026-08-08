package com.example.shopapi.discounts.strategies;

import com.example.shopapi.discounts.dto.DiscountResult;
import com.example.shopapi.productVariant.entities.ProductVariant;
import com.example.shopapi.discounts.interfaces.DiscountCombinationStrategy;
import com.example.shopapi.discounts.interfaces.DiscountStackingRule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DefaultDiscountCombinationStrategy
        implements DiscountCombinationStrategy {

    private final List<DiscountStackingRule> rules;

    @Override
    public List<DiscountResult> combine(
            List<DiscountResult> discounts,
            ProductVariant variant
    ) {
        List<DiscountResult> sorted =
                discounts.stream()
                        .sorted(
                                Comparator.comparing(
                                                DiscountResult::priority
                                        )
                                        .reversed()
                        )
                        .toList();

        List<DiscountResult> selected = new ArrayList<>();

        for (DiscountResult discount : sorted) {
            boolean allowed =
                    rules.stream()
                            .allMatch(
                                    rule ->
                                            rule.isApplicable(
                                                    discount,
                                                    selected
                                            )
                            );

            if (allowed) {
                selected.add(discount);
            }
        }

        return selected;
    }
}