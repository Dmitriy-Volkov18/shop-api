package com.example.shopapi.common.infrastructure.redis;

import com.example.shopapi.product.dto.TrendingScore;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RedisTrendingService {

    private final RedisService redisService;
    private final RedisKeyBuilder keyBuilder;

    public void increaseScore(
            Long productId,
            double score
    ) {

        redisService.zIncrementScore(
                keyBuilder.trendingProducts(),
                productId,
                score
        );
    }

    public List<Long> getTrendingIds(
            Pageable pageable
    ) {

        long start = pageable.getOffset();

        long end =
                start + pageable.getPageSize() - 1;

        Set<Object> values =
                redisService.zReverseRange(
                        keyBuilder.trendingProducts(),
                        start,
                        end
                );

        if (values == null || values.isEmpty()) {
            return List.of();
        }

        return values.stream()
                .map(Long.class::cast)
                .toList();
    }

    public long count() {

        Long count =
                redisService.zCard(
                        keyBuilder.trendingProducts()
                );

        return count == null
                ? 0
                : count;
    }

    public void removeProduct(
            Long productId
    ) {

        redisService.zRemove(
                keyBuilder.trendingProducts(),
                productId
        );
    }

    public void replaceTrending(
            List<TrendingScore> scores
    ) {

        String tempKey =
                keyBuilder.trendingProductsTemp();

        String targetKey =
                keyBuilder.trendingProducts();


        redisService.delete(tempKey);

        if (scores.isEmpty()) {

            redisService.delete(targetKey);

            return;
        }

        for (TrendingScore score : scores) {

            redisService.zAdd(
                    tempKey,
                    score.productId(),
                    score.score()
            );
        }

        redisService.rename(
                tempKey,
                targetKey
        );
    }
}