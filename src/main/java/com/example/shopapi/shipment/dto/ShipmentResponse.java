package com.example.shopapi.shipment.dto;

import com.example.shopapi.shipment.ShipmentStatus;

import java.time.LocalDateTime;

public record ShipmentResponse(
        Long id,
        Long orderId,
        ShipmentStatus status,
        String carrier,
        String trackingNumber,
        LocalDateTime shippedAt,
        LocalDateTime deliveredAt
) {}