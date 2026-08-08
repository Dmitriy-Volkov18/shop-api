package com.example.shopapi.order.dto;

import com.example.shopapi.order.enums.ReorderSkipReason;

public record SkippedReorderItem(
        Long variantId,
        String productName,
        String sku,
        Integer requestedQuantity,
        ReorderSkipReason reason
) {
}