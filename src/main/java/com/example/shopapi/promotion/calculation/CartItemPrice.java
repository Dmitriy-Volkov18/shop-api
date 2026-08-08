package com.example.shopapi.promotion.calculation;

import java.math.BigDecimal;

public record CartItemPrice(
        BigDecimal originalUnitPrice,
        BigDecimal discountAmount,
        BigDecimal finalUnitPrice,
        BigDecimal subtotal
) {
}