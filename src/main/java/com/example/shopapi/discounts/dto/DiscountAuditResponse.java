package com.example.shopapi.discounts.dto;

import com.example.shopapi.discounts.enums.DiscountAuditAction;

import java.time.LocalDateTime;

public record DiscountAuditResponse(
        Long id,
        Long discountId,
        DiscountAuditAction action,
        Long userId,
        LocalDateTime createdAt,
        String details
) {
}