package com.example.shopapi.order.dto;

public record OrderAddressResponse(
        String country,
        String city,
        String street,
        String house,
        String apartment,
        String postalCode
){}