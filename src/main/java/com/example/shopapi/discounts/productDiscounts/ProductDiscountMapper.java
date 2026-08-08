package com.example.shopapi.discounts.productDiscounts;

import com.example.shopapi.discounts.dto.CreateDiscountRequest;
import com.example.shopapi.discounts.productDiscounts.dto.ProductDiscountResponse;
import com.example.shopapi.discounts.dto.UpdateDiscountRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductDiscountMapper {

    ProductDiscountResponse toResponse(
            ProductDiscount discount
    );

    @Mapping(
            target = "id",
            ignore = true
    )
    @Mapping(
            target = "variant",
            ignore = true
    )
    ProductDiscount toEntity(
            CreateDiscountRequest request
    );

    @Mapping(
            target = "id",
            ignore = true
    )
    @Mapping(
            target = "variant",
            ignore = true
    )
    void updateEntity(
            UpdateDiscountRequest request,
            @MappingTarget ProductDiscount discount
    );

}