package com.example.shopapi.category.dto;

import java.util.List;

public record CategoryTreeResponse(
        Long id,
        String name,
        List<CategoryTreeResponse> children
) {
}