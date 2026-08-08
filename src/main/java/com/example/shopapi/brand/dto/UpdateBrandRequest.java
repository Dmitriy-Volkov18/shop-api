package com.example.shopapi.brand.dto;

public record UpdateBrandRequest(
        String name,
        String description,
        String logoUrl,
        String website
) {

}
