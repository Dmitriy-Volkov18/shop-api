package com.example.shopapi.promotion.schedular;

import com.example.shopapi.promotion.repositories.PromotionRepository;
import com.example.shopapi.promotion.enums.PromotionStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PromotionScheduler {

    private final PromotionRepository repository;

    @Scheduled(
            cron = "0 0 * * * *"
    )
    @Transactional
    public void process() {
        LocalDateTime now = LocalDateTime.now();

        repository.activateScheduled(
                now,
                PromotionStatus.SCHEDULED,
                PromotionStatus.ACTIVE
        );

        repository.expireActive(
                now,
                PromotionStatus.ACTIVE,
                PromotionStatus.EXPIRED
        );
    }
}