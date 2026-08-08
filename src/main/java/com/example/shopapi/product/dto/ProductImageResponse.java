package com.example.shopapi.product.dto;

public record ProductImageResponse(
        Long id,
        String imageUrl,
        String fileName,
        String contentType,
        Long fileSize,
        Integer width,
        Integer height,
        boolean primaryImage,
        Integer sortOrder
) {
}