package com.example.shopapi.user.dto;

public record AddressResponse(

        Long id,

        String country,

        String city,

        String street,

        String house,

        String apartment,

        String postalCode,

        boolean primaryAddress

){}