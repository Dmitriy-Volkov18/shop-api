package com.example.shopapi.product.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateProductRequest(

        @NotBlank(message = "Product name is required")
        @Size(
                min = 2,
                max = 250,
                message = "Product name must contain between 2 and 250 characters"
        )
        String name,

        @NotNull(message = "Price is required")
        @Positive(message = "Price must be greater than 0")
        @Digits(
                integer = 10,
                fraction = 2,
                message = "Price format is invalid"
        )
        BigDecimal price,

        @NotNull
        @Min(0)
        Integer stockQuantity,

        @NotNull(message = "Category is required")
        Long categoryId,

        @NotNull(message = "Brand is required")
        Long brandId

) {
}