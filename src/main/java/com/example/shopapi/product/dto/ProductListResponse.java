package com.example.shopapi.product.dto;

import com.example.shopapi.product.enums.ProductStatus;

import java.math.BigDecimal;

public record ProductListResponse(
        Long id,
        String name,
        BigDecimal price,
        String brand,
        String category,
        String seller,
        ProductStatus status,
        String mainImage,
        BigDecimal averageRating,
        Integer reviewCount,
        boolean favorite
) {
}