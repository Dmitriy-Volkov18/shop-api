package com.example.shopapi.promotion.campaign;

import com.example.shopapi.common.exception.BadRequestException;
import com.example.shopapi.promotion.campaign.dto.CreatePromotionCampaignRequest;
import com.example.shopapi.promotion.campaign.dto.UpdatePromotionCampaignRequest;
import com.example.shopapi.promotion.entities.Promotion;
import com.example.shopapi.promotion.enums.PromotionStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PromotionCampaignService {

    private final PromotionCampaignRepository repository;
    private final PromotionCampaignMapper mapper;
    private final PromotionCampaignValidationService validationService;

    @Transactional(readOnly = true)
    public List<PromotionCampaign> getCampaigns() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public PromotionCampaign getCampaign(
            Long id
    ) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new BadRequestException(
                                "Campaign not found"
                        )
                );
    }

    public PromotionCampaign create(
            CreatePromotionCampaignRequest request
    ) {
        if(repository.existsByNameIgnoreCase(
                request.name()
        )){
            throw new BadRequestException(
                    "Campaign already exists"
            );
        }

        PromotionCampaign campaign =
                mapper.toEntity(
                        request
                );

        campaign.setStatus(PromotionStatus.DRAFT);
        validationService.validate(campaign);

        return repository.save(campaign);
    }

    public PromotionCampaign update(
            PromotionCampaign campaign,
            UpdatePromotionCampaignRequest request
    ) {
        if(!campaign.getName()
                .equalsIgnoreCase(request.name())
                &&
                repository.existsByNameIgnoreCase(
                        request.name()
                )){

            throw new BadRequestException(
                    "Campaign already exists"
            );
        }

        mapper.updateEntity(
                request,
                campaign
        );

        validationService.validate(campaign);

        return repository.save(
                campaign
        );
    }


    public void delete(
            PromotionCampaign campaign
    ) {
        repository.delete( campaign);
    }

    @Transactional
    public void publish(
            PromotionCampaign campaign
    ) {
        if(campaign.getStatus() != PromotionStatus.DRAFT){
            throw new BadRequestException(
                    "Only draft campaign can be published"
            );
        }

        LocalDateTime now = LocalDateTime.now();

        PromotionStatus status =
                campaign.getStartsAt().isAfter(now)
                        ? PromotionStatus.SCHEDULED
                        : PromotionStatus.ACTIVE;

        changeStatus(
                campaign,
                status
        );

        campaign.getPromotions()
                .forEach(promotion ->
                        promotion.setStatus(
                                status
                        )
                );
    }

    @Transactional
    public void pause(
            PromotionCampaign campaign
    ) {
        if(campaign.getStatus() != PromotionStatus.ACTIVE){
            throw new BadRequestException(
                    "Only active campaign can be paused"
            );
        }

        changeStatus(
                campaign,
                PromotionStatus.PAUSED
        );

        campaign.getPromotions()
                .forEach(promotion ->
                        promotion.setStatus(
                                PromotionStatus.PAUSED
                        )
                );
    }

    @Transactional
    public void activate(
            PromotionCampaign campaign
    ) {
        if(campaign.getStatus() != PromotionStatus.PAUSED){
            throw new BadRequestException(
                    "Only paused campaign can be activated"
            );
        }

        changeStatus(
                campaign,
                PromotionStatus.ACTIVE
        );

        campaign.getPromotions()
                .forEach(promotion ->
                        promotion.setStatus(
                                PromotionStatus.ACTIVE
                        )
                );
    }

    @Transactional
    public void expire(
            PromotionCampaign campaign
    ) {
        changeStatus(
                campaign,
                PromotionStatus.EXPIRED
        );
    }

    private void changeStatus(
            PromotionCampaign campaign,
            PromotionStatus status
    ) {
        campaign.setStatus(status);

        campaign.getPromotions()
                .forEach(promotion ->
                        promotion.setStatus(
                                status
                        )
                );
    }

    @Transactional
    public void addPromotion(
            PromotionCampaign campaign,
            Promotion promotion
    ) {
        if(campaign.getStatus() != PromotionStatus.DRAFT) {
            throw new BadRequestException(
                    "Cannot modify active campaign"
            );
        }

        if(promotion.getCampaign() != null) {
            throw new BadRequestException(
                    "Promotion already belongs to campaign"
            );
        }

        campaign.addPromotion(promotion);
    }

    @Transactional
    public void removePromotion(
            PromotionCampaign campaign,
            Promotion promotion
    ) {
        if(campaign.getStatus() != PromotionStatus.DRAFT) {
            throw new BadRequestException(
                    "Cannot modify active campaign"
            );
        }

        campaign.removePromotion(promotion);
    }
}