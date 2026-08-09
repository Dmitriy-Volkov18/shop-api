package com.example.shopapi.coupon.dto;

import com.example.shopapi.user.entities.User;

import java.math.BigDecimal;

public record CouponApplicationRequest(
        User user,
        String code,
        BigDecimal orderTotal
) {
}