package com.example.shopapi.brand.dto;

public record BrandResponse(
        Long id,
        String name,
        String description,
        String logoUrl,
        String website,
        boolean active
) {
}