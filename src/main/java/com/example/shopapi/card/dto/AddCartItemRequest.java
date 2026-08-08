package com.example.shopapi.card.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AddCartItemRequest(

        @NotNull
        Long variantId,

        @NotNull
        @Min(1)
        Integer quantity

) {
}