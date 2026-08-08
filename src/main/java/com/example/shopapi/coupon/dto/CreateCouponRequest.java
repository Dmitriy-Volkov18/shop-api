package com.example.shopapi.coupon.dto;

import com.example.shopapi.discounts.enums.DiscountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateCouponRequest(

        @NotBlank
        @Size(max = 100)
        String code,

        @NotNull
        DiscountType type,

        @NotNull
        @Positive
        BigDecimal discountValue,

        @Size(max = 200)
        String description,

        @NotNull
        LocalDateTime startsAt,

        @NotNull
        LocalDateTime endsAt,

        Integer priority,

        BigDecimal minimumOrderAmount,

        BigDecimal maximumDiscountAmount,

        Integer usageLimit,

        Integer perUserLimit,

        Boolean stackable

) {
}