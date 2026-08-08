package com.example.shopapi.product.dto;

public record ProductRatingSummary(
        long reviewCount,
        Double  averageRating

) {
}