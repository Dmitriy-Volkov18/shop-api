package com.example.shopapi.product.cache;

import com.example.shopapi.common.infrastructure.redis.RedisKeyBuilder;
import com.example.shopapi.common.infrastructure.redis.RedisService;
import com.example.shopapi.product.dto.ProductListResponsePage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class ProductListCacheService {


    private static final Duration TTL =
            Duration.ofMinutes(10);


    private final RedisService redisService;
    private final RedisKeyBuilder keyBuilder;


    public ProductListResponsePage get(
            String hash
    ) {

        return redisService.get(
                keyBuilder.products(hash),
                ProductListResponsePage.class
        );
    }


    public void put(
            String hash,
            ProductListResponsePage response
    ) {

        redisService.set(
                keyBuilder.products(hash),
                response,
                TTL
        );
    }


    public void evictAll() {

        redisService.deleteByPattern(
                "*:cache:products:*"
        );
    }
}