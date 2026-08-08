package com.example.shopapi.productVariant.dto;

import com.example.shopapi.productVariant.interfaces.VariantAttributeRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateVariantAttributeRequest(

        @NotBlank(message = "Attribute name is required")
        @Size(
                max = 100,
                message = "Attribute name must not exceed 100 characters"
        )
        String name,

        @NotBlank(message = "Attribute value is required")
        @Size(
                max = 250,
                message = "Attribute value must not exceed 250 characters"
        )
        String value

) implements VariantAttributeRequest {
}