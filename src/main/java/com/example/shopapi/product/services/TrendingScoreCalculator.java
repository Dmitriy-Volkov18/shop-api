package com.example.shopapi.product.services;

import com.example.shopapi.common.config.TrendingProperties;
import com.example.shopapi.product.enums.ProductEventType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TrendingScoreCalculator {

    private final TrendingProperties properties;

    public double calculate(
            ProductEventType type,
            int quantity
    ) {

        return switch (type) {

            case PURCHASE ->
                    quantity * properties.purchaseWeight();

            case WISHLIST ->
                    properties.wishlistWeight();

            case VIEW ->
                    properties.viewWeight();
        };
    }
}