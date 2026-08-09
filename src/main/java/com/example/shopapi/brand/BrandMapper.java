package com.example.shopapi.brand;

import com.example.shopapi.brand.dto.BrandResponse;
import com.example.shopapi.brand.dto.CreateBrandRequest;
import com.example.shopapi.brand.dto.UpdateBrandRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(
        componentModel = "spring"
)
public interface BrandMapper {

    Brand toEntity(
            CreateBrandRequest request
    );

    BrandResponse toResponse(
            Brand brand
    );

    void updateEntity(
            UpdateBrandRequest request,
            @MappingTarget Brand brand
    );
}