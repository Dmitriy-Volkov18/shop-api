package com.example.shopapi.discounts.interfaces;

import com.example.shopapi.discounts.dto.DiscountResult;
import com.example.shopapi.productVariant.entities.ProductVariant;

import java.util.List;
import java.util.Optional;

public interface DiscountSelectionStrategy {

    Optional<DiscountResult> select(
            List<DiscountResult> discounts,
            ProductVariant variant
    );

}