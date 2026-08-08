package com.example.shopapi.discounts;

import com.example.shopapi.discounts.dto.DiscountResult;
import com.example.shopapi.discounts.services.DiscountApplicabilityChecker;
import com.example.shopapi.productVariant.entities.ProductVariant;
import com.example.shopapi.discounts.interfaces.DiscountProvider;
import com.example.shopapi.discounts.interfaces.DiscountSelectionStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DiscountResolver {

    private final List<DiscountProvider> providers;
    private final DiscountSelectionStrategy strategy;
    private final DiscountApplicabilityChecker checker;

    public Optional<DiscountResult> resolve(
            ProductVariant variant
    ){
        List<DiscountResult> discounts =
                providers.stream()
                        .map(provider ->
                                provider.findDiscount(
                                        variant
                                )
                        )
                        .flatMap(Optional::stream)
                        .filter(discount ->
                                checker.isApplicable(
                                        discount,
                                        variant
                                )
                        )
                        .toList();

        return strategy.select(
                discounts,
                variant
        );
    }

    public List<DiscountResult> resolveAll(
            ProductVariant variant
    ) {
        return providers.stream()
                .flatMap(provider ->
                        provider.findDiscounts(variant).stream()
                )
                .toList();
    }
}