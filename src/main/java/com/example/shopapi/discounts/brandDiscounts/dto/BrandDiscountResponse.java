package com.example.shopapi.discounts.brandDiscounts.dto;

import com.example.shopapi.discounts.enums.DiscountStatus;
import com.example.shopapi.discounts.enums.DiscountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BrandDiscountResponse(
        Long id,
        Long brandId,
        String brand,
        DiscountType type,
        BigDecimal discountValue,
        String description,
        LocalDateTime startsAt,
        LocalDateTime endsAt,
        DiscountStatus status,
        Integer priority,
        Integer applicationOrder,
        boolean stackable,
        boolean exclusive
) {
}