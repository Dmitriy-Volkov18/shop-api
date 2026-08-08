package com.example.shopapi.promotion.campaign;

import com.example.shopapi.promotion.enums.PromotionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PromotionCampaignRepository
        extends JpaRepository<PromotionCampaign, Long> {

    boolean existsByNameIgnoreCase(
            String name
    );

    List<PromotionCampaign>
    findByStatusAndStartsAtLessThanEqual(
            PromotionStatus status,
            LocalDateTime now
    );

    List<PromotionCampaign>
    findByStatusAndEndsAtBefore(
            PromotionStatus status,
            LocalDateTime now
    );
}