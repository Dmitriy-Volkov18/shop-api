package com.example.shopapi.order.dto;

import java.math.BigDecimal;

public record CustomerOrderItemResponse(
        Long variantId,
        String productName,
        String sku,
        String imageUrl,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal totalPrice
) {}