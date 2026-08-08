package com.example.shopapi.promotion;

import com.example.shopapi.promotion.dto.CreatePromotionRequest;
import com.example.shopapi.promotion.dto.PromotionResponse;
import com.example.shopapi.promotion.dto.UpdatePromotionRequest;
import com.example.shopapi.promotion.entities.Promotion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PromotionMapper {

    PromotionResponse toResponse(
            Promotion promotion
    );

    @Mapping(
            target = "id",
            ignore = true
    )
    @Mapping(
            target = "version",
            ignore = true
    )
    @Mapping(
            target = "status",
            ignore = true
    )
    Promotion toEntity(
            CreatePromotionRequest request
    );

    @Mapping(
            target = "id",
            ignore = true
    )
    @Mapping(
            target = "version",
            ignore = true
    )
    void updateEntity(
            UpdatePromotionRequest request,
            @MappingTarget Promotion promotion
    );
}