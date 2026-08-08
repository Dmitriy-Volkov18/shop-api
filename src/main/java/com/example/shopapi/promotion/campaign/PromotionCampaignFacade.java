package com.example.shopapi.promotion.campaign;

import com.example.shopapi.promotion.campaign.dto.CreatePromotionCampaignRequest;
import com.example.shopapi.promotion.campaign.dto.PromotionCampaignResponse;
import com.example.shopapi.promotion.campaign.dto.UpdatePromotionCampaignRequest;
import com.example.shopapi.promotion.entities.Promotion;
import com.example.shopapi.promotion.services.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PromotionCampaignFacade {

    private final PromotionCampaignService campaignService;
    private final PromotionCampaignMapper mapper;
    private final PromotionService promotionService;

    @Transactional(readOnly = true)
    public List<PromotionCampaignResponse> getCampaigns() {

        return campaignService.getCampaigns()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }


    @Transactional(readOnly = true)
    public PromotionCampaignResponse getCampaign(
            Long id
    ) {

        return mapper.toResponse(
                campaignService.getCampaign(id)
        );
    }


    public PromotionCampaignResponse create(
            CreatePromotionCampaignRequest request
    ) {

        return mapper.toResponse(
                campaignService.create(request)
        );
    }


    public PromotionCampaignResponse update(
            Long id,
            UpdatePromotionCampaignRequest request
    ) {

        PromotionCampaign campaign =
                campaignService.getCampaign(id);

        return mapper.toResponse(
                campaignService.update(
                        campaign,
                        request
                )
        );
    }


    public void delete(
            Long id
    ) {

        campaignService.delete(
                campaignService.getCampaign(id)
        );
    }


    public PromotionCampaignResponse publish(
            Long id
    ) {

        PromotionCampaign campaign =
                campaignService.getCampaign(id);

        campaignService.publish(campaign);

        return mapper.toResponse(campaign);
    }


    public PromotionCampaignResponse pause(
            Long id
    ) {

        PromotionCampaign campaign =
                campaignService.getCampaign(id);

        campaignService.pause(campaign);

        return mapper.toResponse(campaign);
    }


    public PromotionCampaignResponse activate(
            Long id
    ) {

        PromotionCampaign campaign =
                campaignService.getCampaign(id);

        campaignService.activate(campaign);

        return mapper.toResponse(campaign);
    }

    public PromotionCampaignResponse addPromotion(
            Long campaignId,
            Long promotionId
    ) {

        PromotionCampaign campaign =
                campaignService.getCampaign(
                        campaignId
                );


        Promotion promotion =
                promotionService.getPromotion(
                        promotionId
                );


        campaignService.addPromotion(
                campaign,
                promotion
        );


        return mapper.toResponse(
                campaign
        );
    }

    public PromotionCampaignResponse removePromotion(
            Long campaignId,
            Long promotionId
    ) {

        PromotionCampaign campaign =
                campaignService.getCampaign(
                        campaignId
                );


        Promotion promotion =
                promotionService.getPromotion(
                        promotionId
                );


        campaignService.removePromotion(
                campaign,
                promotion
        );


        return mapper.toResponse(
                campaign
        );
    }
}