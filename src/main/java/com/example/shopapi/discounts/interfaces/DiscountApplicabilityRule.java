package com.example.shopapi.discounts.interfaces;

import com.example.shopapi.discounts.dto.DiscountResult;
import com.example.shopapi.productVariant.entities.ProductVariant;

public interface DiscountApplicabilityRule {

    boolean isApplicable(
            DiscountResult discount,
            ProductVariant variant
    );

}