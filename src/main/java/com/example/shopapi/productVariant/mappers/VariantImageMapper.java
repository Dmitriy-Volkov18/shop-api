package com.example.shopapi.productVariant.mappers;

import com.example.shopapi.productVariant.dto.VariantImageResponse;
import com.example.shopapi.productVariant.entities.VariantImage;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VariantImageMapper {

    VariantImageResponse toResponse(
            VariantImage image
    );

}