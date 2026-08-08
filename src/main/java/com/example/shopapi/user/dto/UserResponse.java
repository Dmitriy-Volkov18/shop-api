package com.example.shopapi.user.dto;

import com.example.shopapi.user.enums.UserRole;

public record UserResponse(
        Long id,
        String username,
        String email,
        UserRole role
) {}