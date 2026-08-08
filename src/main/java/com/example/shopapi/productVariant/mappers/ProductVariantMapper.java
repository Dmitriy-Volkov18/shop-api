package com.example.shopapi.productVariant.mappers;

import com.example.shopapi.discounts.productDiscounts.ProductDiscountMapper;
import com.example.shopapi.productVariant.dto.CreateProductVariantRequest;
import com.example.shopapi.productVariant.dto.ProductVariantResponse;
import com.example.shopapi.productVariant.dto.UpdateProductVariantRequest;
import com.example.shopapi.productVariant.dto.VariantDimensionsRequest;
import com.example.shopapi.productVariant.dto.VariantDimensionsResponse;
import com.example.shopapi.productVariant.entities.ProductVariant;
import com.example.shopapi.productVariant.entities.VariantDimensions;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueMappingStrategy;

@Mapper(
        componentModel = "spring",
        uses = {
                VariantAttributeMapper.class,
                VariantImageMapper.class,
                ProductDiscountMapper.class
        }
)
public interface ProductVariantMapper {


    @Mapping(
            target = "availableQuantity",
            expression = "java(variant.getAvailableQuantity())"
    )
    @Mapping(
            target = "effectivePrice",
            ignore = true
    )
    @Mapping(
            target = "activeDiscount",
            ignore = true
    )
    ProductVariantResponse toResponse(
            ProductVariant variant
    );


    VariantDimensionsResponse toResponse(
            VariantDimensions dimensions
    );


    @BeanMapping(
            nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL
    )
    VariantDimensions toDimensions(
            VariantDimensionsRequest request
    );


    @Mapping(
            target = "id",
            ignore = true
    )
    @Mapping(
            target = "product",
            ignore = true
    )
    @Mapping(
            target = "attributes",
            ignore = true
    )
    ProductVariant toEntity(
            CreateProductVariantRequest request
    );


    @Mapping(
            target = "id",
            ignore = true
    )
    @Mapping(
            target = "product",
            ignore = true
    )
    @Mapping(
            target = "attributes",
            ignore = true
    )
    void updateEntity(
            UpdateProductVariantRequest request,
            @MappingTarget ProductVariant variant
    );

}