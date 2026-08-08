package com.example.shopapi.discounts.interfaces;

import com.example.shopapi.discounts.dto.DiscountResult;
import com.example.shopapi.productVariant.entities.ProductVariant;

import java.util.List;
import java.util.Optional;

public interface DiscountProvider {

    Optional<DiscountResult> findDiscount(
            ProductVariant variant
    );

    default List<DiscountResult> findDiscounts(
            ProductVariant variant
    ) {
        return findDiscount(variant)
                .stream()
                .toList();
    }
}