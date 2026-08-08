package com.example.shopapi.promotion.engine;

import java.math.BigDecimal;

public record PromotionDiscountLine(
        Long variantId,
        int quantity,
        BigDecimal discountAmount
) {
}