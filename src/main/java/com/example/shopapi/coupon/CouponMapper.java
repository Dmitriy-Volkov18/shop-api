package com.example.shopapi.coupon;

import com.example.shopapi.coupon.dto.CouponResponse;
import com.example.shopapi.coupon.dto.CreateCouponRequest;
import com.example.shopapi.coupon.dto.UpdateCouponRequest;
import com.example.shopapi.coupon.entities.Coupon;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CouponMapper {

    CouponResponse toResponse(
            Coupon coupon
    );

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usedCount", ignore = true)
    Coupon toEntity(
            CreateCouponRequest request
    );

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usedCount", ignore = true)
    void updateEntity(
            UpdateCouponRequest request,
            @MappingTarget Coupon coupon
    );
}