package com.example.shopapi.product.recentlyviewed;

import com.example.shopapi.common.infrastructure.redis.RedisKeyBuilder;
import com.example.shopapi.common.infrastructure.redis.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RedisRecentlyViewedService {


    private static final int LIMIT = 20;
    private static final Duration TTL =
            Duration.ofDays(90);


    private final RedisService redisService;
    private final RedisKeyBuilder keyBuilder;


    public void add(
            Long userId,
            Long productId
    ) {

        String key =
                keyBuilder.recentlyViewed(userId);


        double score =
                System.currentTimeMillis();


        redisService.zAdd(
                key,
                productId,
                score
        );


        trim(key);


        redisService.expire(
                key,
                TTL
        );
    }


    public List<Long> get(
            Long userId
    ) {

        String key =
                keyBuilder.recentlyViewed(userId);


        Set<Object> ids =
                redisService.zReverseRange(
                        key,
                        0,
                        LIMIT - 1
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
                        LIMIT,
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