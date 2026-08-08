package com.example.shopapi.coupon.dto;

import com.example.shopapi.discounts.enums.DiscountStatus;
import com.example.shopapi.discounts.enums.DiscountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CouponResponse(

        Long id,

        String code,

        DiscountType type,

        BigDecimal discountValue,

        String description,

        DiscountStatus status,

        LocalDateTime startsAt,

        LocalDateTime endsAt,

        Integer priority,

        BigDecimal minimumOrderAmount,

        BigDecimal maximumDiscountAmount,

        Integer usageLimit,

        Integer usedCount,

        Integer perUserLimit,

        Boolean stackable

) {
}