package com.example.shopapi.product.mappers;

import com.example.shopapi.product.entities.Product;
import com.example.shopapi.productVariant.mappers.ProductVariantMapper;
import com.example.shopapi.product.dto.CreateProductRequest;
import com.example.shopapi.product.dto.ProductDetailResponse;
import com.example.shopapi.product.dto.ProductListResponse;
import com.example.shopapi.product.dto.UpdateProductRequest;
import com.example.shopapi.category.CategoryMapper;
import com.example.shopapi.product.services.ProductImageService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(
        componentModel = "spring",
        uses = {
                CategoryMapper.class,
                ProductImageMapper.class,
                ProductVariantMapper.class,
                ProductImageService.class
        }
)
public interface ProductMapper {
    @Mapping(
            target = "category",
            source = "category.name"
    )
    @Mapping(
            target = "brand",
            source = "brand.name"
    )
    @Mapping(
            target = "seller",
            source = "user.username"
    )
    @Mapping(
            target = "mainImage",
            source = ".",
            qualifiedByName = "mainProductImage"
    )
    @Mapping(
            target = "favorite",
            ignore = true
    )
    ProductListResponse toListResponse(Product product);

    @Mapping(
            target = "brandId",
            source = "brand.id"
    )
    @Mapping(
            target = "brand",
            source = "brand.name"
    )
    @Mapping(
            target = "category",
            source = "category"
    )
    ProductDetailResponse toDetailResponse(Product product);

    @Mapping(
            target = "id",
            ignore = true
    )
    @Mapping(
            target = "user",
            ignore = true
    )
    @Mapping(
            target = "brand",
            ignore = true
    )
    @Mapping(
            target = "category",
            ignore = true
    )
    @Mapping(
            target = "images",
            ignore = true
    )
    Product toEntity(CreateProductRequest request);


    @Mapping(
            target = "id",
            ignore = true
    )
    @Mapping(
            target = "user",
            ignore = true
    )
    @Mapping(
            target = "brand",
            ignore = true
    )
    @Mapping(
            target = "category",
            ignore = true
    )
    @Mapping(
            target = "images",
            ignore = true
    )
    void updateEntity(
            UpdateProductRequest request,
            @MappingTarget Product product
    );

}