package com.example.shopapi.reviews.dto;

import com.example.shopapi.reviews.ReviewStatus;

import java.time.LocalDateTime;
import java.util.List;

public record ReviewResponse(
        Long id,
        Long productId,
        String username,
        Integer rating,
        String comment,
        ReviewStatus status,
        LocalDateTime createdAt,
        List<ReviewImageResponse> images
) {
}