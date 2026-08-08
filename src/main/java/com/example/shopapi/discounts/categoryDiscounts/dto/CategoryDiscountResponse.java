package com.example.shopapi.discounts.categoryDiscounts.dto;

import com.example.shopapi.discounts.enums.DiscountStatus;
import com.example.shopapi.discounts.enums.DiscountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CategoryDiscountResponse(
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