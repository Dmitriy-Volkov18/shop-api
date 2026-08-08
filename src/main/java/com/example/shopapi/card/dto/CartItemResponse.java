package com.example.shopapi.card.dto;

import java.math.BigDecimal;

public record CartItemResponse(
        Long id,
        Long variantId,
        String productName,
        String sku,
        String image,
        int quantity,
        BigDecimal originalUnitPrice,
        BigDecimal discountAmount,
        BigDecimal finalUnitPrice,
        BigDecimal subtotal
) {
}