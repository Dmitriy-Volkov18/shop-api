package com.example.shopapi.user.dto;

import jakarta.validation.constraints.NotBlank;

public record AddressRequest(

        @NotBlank
        String country,

        @NotBlank
        String city,

        @NotBlank
        String street,

        @NotBlank
        String house,

        String apartment,

        @NotBlank
        String postalCode

){}