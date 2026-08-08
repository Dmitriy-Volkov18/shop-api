package com.example.shopapi.promotion.campaign;

import com.example.shopapi.promotion.campaign.dto.CreatePromotionCampaignRequest;
import com.example.shopapi.promotion.campaign.dto.PromotionCampaignResponse;
import com.example.shopapi.promotion.campaign.dto.UpdatePromotionCampaignRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PromotionCampaignMapper {

    PromotionCampaign toEntity(
            CreatePromotionCampaignRequest request
    );

    void updateEntity(
            UpdatePromotionCampaignRequest request,
            @MappingTarget PromotionCampaign campaign
    );

    @Mapping(
            target = "promotionCount",
            expression = "java(campaign.getPromotions().size())"
    )
    PromotionCampaignResponse toResponse(
            PromotionCampaign campaign
    );
}