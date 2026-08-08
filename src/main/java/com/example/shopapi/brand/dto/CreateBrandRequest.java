package com.example.shopapi.brand.dto;

public record CreateBrandRequest(
        String name,
        String description,
        String logoUrl,
        String website
) {
}