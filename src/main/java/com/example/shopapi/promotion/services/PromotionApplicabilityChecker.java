package com.example.shopapi.promotion.services;

import com.example.shopapi.promotion.entities.Promotion;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class PromotionApplicabilityChecker {

    public boolean isApplicable(
            Promotion promotion,
            LocalDateTime now
    ) {
        if(!promotion.isApplicable(now)) {
            return false;
        }

        if(promotion.hasCampaign()) {
            return promotion.getCampaign().isActive();
        }

        return true;
    }
}