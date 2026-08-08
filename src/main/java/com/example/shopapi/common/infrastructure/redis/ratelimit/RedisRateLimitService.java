package com.example.shopapi.common.infrastructure.redis.ratelimit;

import com.example.shopapi.common.infrastructure.redis.RedisRateLimitScriptService;
import com.example.shopapi.common.infrastructure.redis.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisRateLimitService {

    private final RedisRateLimitScriptService scriptService;

    public boolean tryConsume(
            String key,
            long limit,
            Duration window
    ) {
        Long count =
                scriptService.increment(
                        key,
                        window
                );


        return count <= limit;
    }

    public void reset(
            String key
    ) {
        scriptService.delete(key);
    }

    public long remaining(
            String key,
            long limit
    ) {
        Long count = scriptService.getCount(key);

        if (count == null) {
            return limit;
        }

        return Math.max(
                0,
                limit - count
        );
    }
}