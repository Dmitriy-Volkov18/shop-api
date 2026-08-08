package com.example.shopapi.product.jobs;

import com.example.shopapi.common.config.TrendingProperties;
import com.example.shopapi.common.infrastructure.redis.RedisTrendingService;
import com.example.shopapi.product.dto.TrendingScore;
import com.example.shopapi.product.repositories.ProductEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TrendingRebuildJob {

    private final ProductEventRepository repository;
    private final TrendingProperties properties;
    private final RedisTrendingService redisTrendingService;

    @Scheduled(
            cron = "${shop.trending.rebuild-cron}"
    )
    @Transactional(readOnly = true)
    public void rebuild() {

        List<TrendingScore> scores =
                repository.calculateTrending(
                        LocalDateTime.now()
                                .minusDays(properties.periodDays()),
                        properties.purchaseWeight(),
                        properties.wishlistWeight(),
                        properties.viewWeight()
                );

        redisTrendingService.replaceTrending(scores);
    }
}