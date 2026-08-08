package com.example.shopapi.productVariant.dto;

import java.math.BigDecimal;

public record VariantDimensionsResponse(
        BigDecimal weight,
        BigDecimal width,
        BigDecimal height,
        BigDecimal length
) {
}