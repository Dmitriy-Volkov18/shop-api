package com.example.shopapi.productVariant.mappers;

import com.example.shopapi.productVariant.dto.CreateVariantAttributeRequest;
import com.example.shopapi.productVariant.dto.VariantAttributeResponse;
import com.example.shopapi.productVariant.entities.VariantAttribute;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VariantAttributeMapper {

    VariantAttributeResponse toResponse(
            VariantAttribute attribute
    );

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "variant", ignore = true)
    VariantAttribute toEntity(
            CreateVariantAttributeRequest request
    );
}