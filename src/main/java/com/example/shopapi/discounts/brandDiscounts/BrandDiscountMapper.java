package com.example.shopapi.discounts.brandDiscounts;

import com.example.shopapi.discounts.brandDiscounts.dto.BrandDiscountResponse;
import com.example.shopapi.discounts.brandDiscounts.dto.CreateBrandDiscountRequest;
import com.example.shopapi.discounts.brandDiscounts.dto.UpdateBrandDiscountRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BrandDiscountMapper {

    @Mapping(
            target = "brandId",
            source = "brand.id"
    )
    @Mapping(
            target = "brand",
            source = "brand.name"
    )
    BrandDiscountResponse toResponse(
            BrandDiscount discount
    );

    @Mapping(
            target = "id",
            ignore = true
    )
    @Mapping(
            target = "brand",
            ignore = true
    )
    @Mapping(
            target = "status",
            ignore = true
    )
    BrandDiscount toEntity(
            CreateBrandDiscountRequest request
    );

    @Mapping(
            target = "id",
            ignore = true
    )
    @Mapping(
            target = "brand",
            ignore = true
    )
    @Mapping(
            target = "status",
            ignore = true
    )
    void updateEntity(
            UpdateBrandDiscountRequest request,
            @MappingTarget BrandDiscount discount
    );
}