package com.example.shopapi.discounts.productDiscounts.dto;

import com.example.shopapi.discounts.enums.DiscountStatus;
import com.example.shopapi.discounts.enums.DiscountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductDiscountResponse(
        Long id,
        DiscountType type,
        BigDecimal discountValue,
        String description,
        LocalDateTime startsAt,
        LocalDateTime endsAt,
        DiscountStatus status,
        Integer priority
) {
}