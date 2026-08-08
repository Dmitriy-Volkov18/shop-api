package com.example.shopapi.returnProducts.dto;

import jakarta.validation.constraints.NotBlank;

public record ReturnRequestCreate(

        @NotBlank
        String reason

){}