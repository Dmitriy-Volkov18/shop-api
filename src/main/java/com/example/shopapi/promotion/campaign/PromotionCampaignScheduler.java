package com.example.shopapi.promotion.campaign;

import com.example.shopapi.promotion.enums.PromotionStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PromotionCampaignScheduler {

    private final PromotionCampaignRepository repository;
    private final PromotionCampaignService campaignService;

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void process() {
        LocalDateTime now = LocalDateTime.now();

        repository.findByStatusAndStartsAtLessThanEqual(
                        PromotionStatus.SCHEDULED,
                        now
                )
                .forEach(
                        campaignService::activate
                );

        repository.findByStatusAndEndsAtBefore(
                        PromotionStatus.ACTIVE,
                        now
                )
                .forEach(
                        campaignService::expire
                );
    }
}