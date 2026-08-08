package com.example.shopapi.productVariant.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

public record CreateProductVariantRequest(

        @NotBlank
        @Size(max = 100)
        String sku,

        @Digits(
                integer = 10,
                fraction = 2
        )
        @Positive
        BigDecimal price,

        @NotNull
        @Min(0)
        Integer stockQuantity,

        @Valid
        VariantDimensionsRequest dimensions,

        @NotEmpty
        @Valid
        List<CreateVariantAttributeRequest> attributes

) {
}