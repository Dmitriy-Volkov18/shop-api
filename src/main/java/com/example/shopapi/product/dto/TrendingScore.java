package com.example.shopapi.product.dto;

public record TrendingScore(
        Long productId,
        double score
) {
}