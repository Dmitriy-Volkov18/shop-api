package com.example.shopapi.product.dto;

import com.example.shopapi.category.dto.CategoryResponse;
import com.example.shopapi.productVariant.dto.ProductVariantResponse;
import com.example.shopapi.product.enums.ProductStatus;

import java.math.BigDecimal;
import java.util.List;

public record ProductDetailResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        String sku,
        Long brandId,
        String brand,
        Integer stockQuantity,
        ProductStatus status,
        CategoryResponse category,
        List<ProductImageResponse> images,
        List<ProductVariantResponse> variants,
        BigDecimal averageRating,
        Integer reviewCount
) {
}