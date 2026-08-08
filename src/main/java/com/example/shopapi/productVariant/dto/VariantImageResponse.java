package com.example.shopapi.productVariant.dto;

public record VariantImageResponse(
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