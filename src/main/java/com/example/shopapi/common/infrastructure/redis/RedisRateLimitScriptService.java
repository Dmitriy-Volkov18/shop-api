package com.example.shopapi.common.infrastructure.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RedisRateLimitScriptService {

    private final RedisTemplate<String,Object> redisTemplate;
    private final RedisScript<Long> rateLimitScript;

    public Long increment(
            String key,
            Duration window
    ) {
        return redisTemplate.execute(
                rateLimitScript,
                List.of(key),
                String.valueOf(
                        window.toSeconds()
                )
        );
    }

    public void delete(
            String key
    ) {
        redisTemplate.delete(key);
    }

    public Long getCount(
            String key
    ) {
        Object value = redisTemplate.opsForValue().get(key);

        if (value == null) {
            return null;
        }

        return Long.valueOf(value.toString());
    }
}