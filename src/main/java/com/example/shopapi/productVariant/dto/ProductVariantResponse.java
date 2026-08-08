package com.example.shopapi.productVariant.dto;

import com.example.shopapi.discounts.dto.ActiveDiscountResponse;
import com.example.shopapi.productVariant.enums.ProductVariantStatus;

import java.math.BigDecimal;
import java.util.List;

public record ProductVariantResponse(
        Long id,
        String sku,
        BigDecimal price,
        BigDecimal effectivePrice,
        ActiveDiscountResponse activeDiscount,
        Integer availableQuantity,
        ProductVariantStatus status,
        VariantDimensionsResponse dimensions,
        List<VariantAttributeResponse> attributes,
        List<VariantImageResponse> images
) {
}