package com.example.shopapi.category.dto;

public record CategoryResponse(
        Long id,
        String name,
        Long parentId
){}