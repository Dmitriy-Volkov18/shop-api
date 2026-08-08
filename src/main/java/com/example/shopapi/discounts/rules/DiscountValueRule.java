package com.example.shopapi.discounts.rules;

import com.example.shopapi.discounts.dto.DiscountResult;
import com.example.shopapi.productVariant.entities.ProductVariant;
import com.example.shopapi.discounts.interfaces.DiscountApplicabilityRule;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DiscountValueRule
        implements DiscountApplicabilityRule {

    @Override
    public boolean isApplicable(
            DiscountResult discount,
            ProductVariant variant
    ) {
        BigDecimal basePrice =
                variant.getPrice() != null
                        ? variant.getPrice()
                        : variant.getProduct().getPrice();

        if(discount.isPercent()) {
            return discount.discountValue()
                    .compareTo(
                            BigDecimal.valueOf(100)
                    ) <= 0;
        }

        return discount.discountValue()
                .compareTo(basePrice)
                <= 0;
    }
}