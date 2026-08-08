package com.example.shopapi.discounts.categoryDiscounts;

import com.example.shopapi.discounts.categoryDiscounts.dto.CategoryDiscountResponse;
import com.example.shopapi.discounts.categoryDiscounts.dto.CreateCategoryDiscountRequest;
import com.example.shopapi.discounts.categoryDiscounts.dto.UpdateCategoryDiscountRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CategoryDiscountMapper {


    CategoryDiscountResponse toResponse(
            CategoryDiscount discount
    );


    @Mapping(
            target = "id",
            ignore = true
    )
    @Mapping(
            target = "category",
            ignore = true
    )
    CategoryDiscount toEntity(
            CreateCategoryDiscountRequest request
    );


    @Mapping(
            target = "id",
            ignore = true
    )
    @Mapping(
            target = "category",
            ignore = true
    )
    void updateEntity(
            UpdateCategoryDiscountRequest request,
            @MappingTarget CategoryDiscount discount
    );
}