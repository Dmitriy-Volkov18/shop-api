package com.example.shopapi.product.services;

import com.example.shopapi.discounts.services.DiscountStackingService;
import com.example.shopapi.productVariant.entities.ProductVariant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ProductPricingService {

    private final DiscountStackingService discountStackingService;

    public BigDecimal getBasePrice(
            ProductVariant variant
    ) {
        return variant.getPrice() != null
                ? variant.getPrice()
                : variant.getProduct().getPrice();
    }

    public BigDecimal calculateSubtotal(
            BigDecimal unitPrice,
            int quantity
    ) {
        return unitPrice.multiply(
                BigDecimal.valueOf(quantity)
        );
    }

    public BigDecimal calculateEffectivePrice(
            ProductVariant variant
    ) {
        return discountStackingService.calculate(
                variant,
                getBasePrice(variant)
        );
    }

}