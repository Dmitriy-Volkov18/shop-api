package com.example.shopapi.promotion.campaign;

import com.example.shopapi.common.exception.runtimeExceptions.BadRequestException;
import org.springframework.stereotype.Service;

@Service
public class PromotionCampaignValidationService {

    public void validate(
            PromotionCampaign campaign
    ) {
        if(campaign.getEndsAt().isBefore(campaign.getStartsAt())) {
            throw new BadRequestException(
                    "Campaign end date must be after start date"
            );
        }
    }
}