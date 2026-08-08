package com.example.shopapi.order.dto;

import com.example.shopapi.card.dto.CartResponse;

import java.util.List;

public record ReorderResult(
        CartResponse cart,
        List<SkippedReorderItem> skippedItems
) {
}