package com.example.shopapi.common.infrastructure.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisBlacklistService {

    private final RedisService redisService;
    private final RedisKeyBuilder keyBuilder;

    public void blacklist(
            String jti,
            Duration ttl
    ) {

        redisService.set(
                keyBuilder.revokedJti(jti),
                true,
                ttl
        );
    }

    public boolean isBlacklisted(
            String jti
    ) {

        return redisService.exists(
                keyBuilder.revokedJti(jti)
        );
    }

    public void remove(
            String jti
    ) {

        redisService.delete(
                keyBuilder.revokedJti(jti)
        );
    }
}