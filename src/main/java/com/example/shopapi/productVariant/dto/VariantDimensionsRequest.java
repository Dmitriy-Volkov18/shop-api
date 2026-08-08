package com.example.shopapi.productVariant.dto;

import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record VariantDimensionsRequest(

        @Positive(message = "Weight must be positive")
        BigDecimal weight,

        @Positive(message = "Width must be positive")
        BigDecimal width,

        @Positive(message = "Height must be positive")
        BigDecimal height,

        @Positive(message = "Length must be positive")
        BigDecimal length

) {
}