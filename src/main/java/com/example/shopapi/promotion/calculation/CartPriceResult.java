package com.example.shopapi.promotion.calculation;

import com.example.shopapi.promotion.engine.AppliedPromotion;

import java.math.BigDecimal;
import java.util.List;

public record CartPriceResult(
        BigDecimal subtotal,
        BigDecimal discountAmount,
        BigDecimal total,
        List<AppliedPromotion> appliedPromotions
) {
}