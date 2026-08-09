package com.example.shopapi.discounts.services;

import com.example.shopapi.discounts.dto.DiscountResult;
import com.example.shopapi.productVariant.entities.ProductVariant;
import com.example.shopapi.discounts.interfaces.DiscountApplicabilityRule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DiscountApplicabilityChecker {

    private final List<DiscountApplicabilityRule> rules;

    public boolean isApplicable(
            DiscountResult discount,
            ProductVariant variant
    ){
        return rules.stream()
                .allMatch(rule ->
                        rule.isApplicable(
                                discount,
                                variant
                        )
                );
    }
}