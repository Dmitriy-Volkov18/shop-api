package com.example.shopapi.shipment.dto;

import jakarta.validation.constraints.NotBlank;

public record ShipRequest(

        @NotBlank
        String carrier,

        @NotBlank
        String trackingNumber

) {}