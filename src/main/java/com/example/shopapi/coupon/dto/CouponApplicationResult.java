package com.example.shopapi.coupon.dto;

import com.example.shopapi.coupon.entities.Coupon;

import java.math.BigDecimal;

public record CouponApplicationResult(

        Coupon coupon,

        BigDecimal discountAmount,

        BigDecimal finalTotal

) {
}