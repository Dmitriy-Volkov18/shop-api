package com.example.shopapi.returnProducts.dto;

import com.example.shopapi.returnProducts.ReturnStatus;

import java.time.LocalDateTime;

public record ReturnResponse(
        Long id,
        Long orderId,
        ReturnStatus status,
        String reason,
        LocalDateTime approvedAt,
        LocalDateTime completedAt
){}
