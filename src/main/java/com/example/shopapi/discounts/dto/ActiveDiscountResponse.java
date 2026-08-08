package com.example.shopapi.discounts.dto;

import com.example.shopapi.discounts.enums.DiscountSource;
import com.example.shopapi.discounts.enums.DiscountType;

import java.math.BigDecimal;

public record ActiveDiscountResponse(
        DiscountSource source,
        DiscountType type,
        BigDecimal discountValue,
        String description,
        Integer priority
) {}