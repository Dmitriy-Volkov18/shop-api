package com.example.shopapi.category.cache;

import com.example.shopapi.category.dto.CategoryResponse;
import com.example.shopapi.category.dto.CategoryTreeResponse;
import com.example.shopapi.common.infrastructure.redis.RedisKeyBuilder;
import com.example.shopapi.common.infrastructure.redis.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryCacheService {

    private static final Duration CATEGORY_TTL = Duration.ofHours(2);

    private final RedisService redisService;
    private final RedisKeyBuilder keyBuilder;

    public CategoryResponse get(
            Long id
    ) {
        return redisService.get(
                keyBuilder.category(id),
                CategoryResponse.class
        );
    }

    public void put(
            CategoryResponse response
    ) {
        redisService.set(
                keyBuilder.category(response.id()),
                response,
                CATEGORY_TTL
        );
    }

    public void evict(
            Long id
    ) {
        redisService.delete(keyBuilder.category(id));
    }

    public List<CategoryTreeResponse> getTree() {
        return redisService.get(
                keyBuilder.categoryTree(),
                new TypeReference<List<CategoryTreeResponse>>() {}
        );
    }

    public void putTree(
            List<CategoryTreeResponse> tree
    ) {
        redisService.set(
                keyBuilder.categoryTree(),
                tree,
                CATEGORY_TTL
        );
    }

    public void evictTree() {
        redisService.delete(keyBuilder.categoryTree());
    }

    public void evictAll(
            Long categoryId
    ) {
        evict(categoryId);
        evictTree();
    }
}