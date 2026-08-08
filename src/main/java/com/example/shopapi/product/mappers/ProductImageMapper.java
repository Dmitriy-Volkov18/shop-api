package com.example.shopapi.product.mappers;

import com.example.shopapi.product.entities.ProductImage;
import com.example.shopapi.product.dto.ProductImageResponse;
import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring"
)
public interface ProductImageMapper {


    ProductImageResponse toResponse(
            ProductImage image
    );

}