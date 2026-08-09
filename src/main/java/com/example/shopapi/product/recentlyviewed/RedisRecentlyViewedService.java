package com.example.shopapi.product.recentlyviewed;

import com.example.shopapi.common.infrastructure.redis.RedisKeyBuilder;
import com.example.shopapi.common.infrastructure.redis.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

import static com.example.shopapi.common.constants.RecentlyViewedConstants.RECENTLY_VIEWED_LIMIT;
import static com.example.shopapi.common.constants.RedisCacheTImeConstants.RECENTLY_VIEWED_TTL;

@Service
@RequiredArgsConstructor
public class RedisRecentlyViewedService {


    private final RedisService redisService;
    private final RedisKeyBuilder keyBuilder;

    public void add(
            Long userId,
            Long productId
    ) {
        String key = keyBuilder.recentlyViewed(userId);

        double score = System.currentTimeMillis();

        redisService.zAdd(
                key,
                productId,
                score
        );

        trim(key);

        redisService.expire(
                key,
                RECENTLY_VIEWED_TTL
        );
    }

    public List<Long> get(
            Long userId
    ) {
        String key = keyBuilder.recentlyViewed(userId);

        Set<Object> ids =
                redisService.zReverseRange(
                        key,
                        0,
                        RECENTLY_VIEWED_LIMIT - 1
                );


        return ids.stream()
                .map(Long.class::cast)
                .toList();
    }

    private void trim(
            String key
    ) {
        Set<Object> extra =
                redisService.zReverseRange(
                        key,
                        RECENTLY_VIEWED_LIMIT,
                        -1
                );

        if(extra != null) {
            extra.forEach(
                    id -> redisService.zRemove(
                            key,
                            id
                    )
            );
        }
    }
}