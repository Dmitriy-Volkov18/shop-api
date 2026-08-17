package com.example.shopapi.promotion.campaign;

import com.example.shopapi.promotion.campaign.dto.CreatePromotionCampaignRequest;
import com.example.shopapi.promotion.campaign.dto.PromotionCampaignResponse;
import com.example.shopapi.promotion.campaign.dto.UpdatePromotionCampaignRequest;
import com.example.shopapi.promotion.entities.Promotion;
import com.example.shopapi.promotion.services.PromotionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
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
        log.info("Promotion campaign is created");

        return mapper.toResponse(
                campaignService.create(request)
        );
    }

    public PromotionCampaignResponse update(
            Long id,
            UpdatePromotionCampaignRequest request
    ) {
        PromotionCampaign campaign = campaignService.getCampaign(id);

        log.info("Promotion campaign is updated");

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
        campaignService.delete(campaignService.getCampaign(id) );

        log.info("Promotion campaign is deleted");
    }

    public PromotionCampaignResponse publish(
            Long id
    ) {
        PromotionCampaign campaign = campaignService.getCampaign(id);

        campaignService.publish(campaign);

        log.info("Promotion campaign is published");

        return mapper.toResponse(campaign);
    }


    public PromotionCampaignResponse pause(
            Long id
    ) {
        PromotionCampaign campaign = campaignService.getCampaign(id);

        campaignService.pause(campaign);

        log.info("Promotion campaign is paused");

        return mapper.toResponse(campaign);
    }

    public PromotionCampaignResponse activate(
            Long id
    ) {
        PromotionCampaign campaign = campaignService.getCampaign(id);

        campaignService.activate(campaign);

        log.info("Promotion campaign is activated");

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

        log.info("Promotion is added to campaign");

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

        log.info("Promotion is removed out of campaign");

        return mapper.toResponse(
                campaign
        );
    }
}