package com.example.shopapi.user.dto;

import com.example.shopapi.user.enums.UserRole;

public record MeResponse(
        Long userId,
        String username,
        UserRole role
) {}