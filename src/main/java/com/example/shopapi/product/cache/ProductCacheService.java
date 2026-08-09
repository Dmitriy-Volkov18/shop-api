package com.example.shopapi.product.cache;

import com.example.shopapi.common.infrastructure.redis.RedisKeyBuilder;
import com.example.shopapi.common.infrastructure.redis.RedisService;
import com.example.shopapi.product.dto.ProductDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.example.shopapi.common.constants.RedisCacheTImeConstants.PRODUCT_TTL;

@Service
@RequiredArgsConstructor
public class ProductCacheService {

    private final RedisService redisService;
    private final RedisKeyBuilder keyBuilder;

    public ProductDetailResponse get(
            Long productId
    ) {
        return redisService.get(
                keyBuilder.product(productId),
                ProductDetailResponse.class
        );
    }

    public void put(
            ProductDetailResponse response
    ) {
        redisService.set(
                keyBuilder.product(response.id()),
                response,
                PRODUCT_TTL
        );
    }

    public void evict(
            Long productId
    ) {
        redisService.delete(keyBuilder.product(productId));
    }
}