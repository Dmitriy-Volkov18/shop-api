package com.example.shopapi.discounts.brandDiscounts.dto;

import com.example.shopapi.discounts.enums.DiscountType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateBrandDiscountRequest(

        @NotNull
        Long brandId,

        @NotNull
        DiscountType type,

        @NotNull
        @Positive
        BigDecimal discountValue,

        String description,

        @NotNull
        LocalDateTime startsAt,

        @NotNull
        LocalDateTime endsAt,

        Integer priority,

        Integer applicationOrder,

        Boolean stackable,

        Boolean exclusive

) {
}