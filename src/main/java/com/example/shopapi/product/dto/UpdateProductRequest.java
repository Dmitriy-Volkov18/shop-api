package com.example.shopapi.product.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record UpdateProductRequest(

        @NotBlank(message = "Product name is required")
        @Size(
                min = 2,
                max = 250
        )
        String name,

        @NotNull(message = "Price is required")
        @Positive
        @Digits(
                integer = 10,
                fraction = 2
        )
        BigDecimal price,

        @NotNull
        @Min(0)
        Integer stockQuantity,

        @NotNull
        Long categoryId,

        @NotNull(message = "Brand is required")
        Long brandId

) {
}