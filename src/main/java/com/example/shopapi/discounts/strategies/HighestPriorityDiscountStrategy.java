package com.example.shopapi.discounts.strategies;

import com.example.shopapi.discounts.dto.DiscountResult;
import com.example.shopapi.productVariant.entities.ProductVariant;
import com.example.shopapi.discounts.interfaces.DiscountSelectionStrategy;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class HighestPriorityDiscountStrategy
        implements DiscountSelectionStrategy {

    @Override
    public Optional<DiscountResult> select(
            List<DiscountResult> discounts,
            ProductVariant variant
    ) {
        return discounts.stream()
                .max(
                        Comparator.comparing(
                                DiscountResult::priority
                        )
                );
    }
}