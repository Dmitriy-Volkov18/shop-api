package com.example.shopapi.discounts.interfaces;

import com.example.shopapi.discounts.dto.DiscountResult;
import com.example.shopapi.productVariant.entities.ProductVariant;

import java.util.List;

public interface DiscountCombinationStrategy {

    List<DiscountResult> combine(
            List<DiscountResult> discounts,
            ProductVariant variant
    );

}