package com.example.shopapi.discounts.rules;

import com.example.shopapi.discounts.dto.DiscountResult;
import com.example.shopapi.productVariant.entities.ProductVariant;
import com.example.shopapi.product.enums.ProductStatus;
import com.example.shopapi.discounts.interfaces.DiscountApplicabilityRule;
import org.springframework.stereotype.Component;

@Component
public class ProductStatusRule
        implements DiscountApplicabilityRule {

    @Override
    public boolean isApplicable(
            DiscountResult discount,
            ProductVariant variant
    ){
        return variant.getProduct().getStatus() == ProductStatus.ACTIVE;
    }
}