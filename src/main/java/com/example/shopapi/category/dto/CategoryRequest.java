package com.example.shopapi.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryRequest(

        @NotBlank
        @Size(
                min = 2,
                max = 100
        )
        String name,

        Long parentId

){}