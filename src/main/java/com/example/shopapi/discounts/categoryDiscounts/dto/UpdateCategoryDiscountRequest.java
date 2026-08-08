package com.example.shopapi.discounts.categoryDiscounts.dto;

import com.example.shopapi.discounts.enums.DiscountStatus;
import com.example.shopapi.discounts.enums.DiscountType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record UpdateCategoryDiscountRequest(

        @NotNull(message = "Discount type is required")
        DiscountType type,

        @NotNull(message = "Discount value is required")
        @Positive(message = "Discount value must be greater than zero")
        BigDecimal discountValue,

        @Size(max = 200)
        String description,

        @NotNull(message = "Start date is required")
        LocalDateTime startsAt,

        @NotNull(message = "End date is required")
        LocalDateTime endsAt,

        Integer priority,
        DiscountStatus status

) {
}