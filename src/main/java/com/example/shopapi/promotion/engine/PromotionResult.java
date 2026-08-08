package com.example.shopapi.promotion.engine;

import java.math.BigDecimal;
import java.util.List;

public record PromotionResult(
        BigDecimal discountAmount,
        boolean freeShipping,
        List<PromotionDiscountLine> discounts
) {
    public static PromotionResult empty() {
        return new PromotionResult(
                BigDecimal.ZERO,
                false,
                List.of()
        );
    }

    public static PromotionResult fixedDiscount(
            BigDecimal amount
    ) {
        return new PromotionResult(
                amount,
                false,
                List.of()
        );
    }

    public static PromotionResult withDiscountLines(
            List<PromotionDiscountLine> lines
    ) {
        return new PromotionResult(
                BigDecimal.ZERO,
                false,
                lines
        );
    }

    public static PromotionResult withFreeShipping() {
        return new PromotionResult(
                BigDecimal.ZERO,
                true,
                List.of()
        );
    }
}