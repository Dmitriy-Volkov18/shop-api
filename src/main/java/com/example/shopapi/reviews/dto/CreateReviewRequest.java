package com.example.shopapi.reviews.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record CreateReviewRequest(

        @Min(1)
        @Max(5)
        Integer rating,

        @Size(max = 2000)
        String comment

) {
}