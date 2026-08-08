package com.example.shopapi.reviews.dto;

public record ReviewImageResponse(
        Long id,
        String imageUrl,
        Integer sortOrder
) {
}