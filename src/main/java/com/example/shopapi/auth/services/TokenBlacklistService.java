package com.example.shopapi.auth.services;

import com.example.shopapi.common.config.JwtProperties;
import com.example.shopapi.common.infrastructure.redis.RedisBlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final RedisBlacklistService redisBlacklistService;
    private final JwtProperties jwtProperties;

    public void revoke(
            String jti
    ) {
        redisBlacklistService.blacklist(
                jti,
                jwtProperties.getAccessExpiration()
        );
    }

    public boolean isRevoked(
            String jti
    ) {
        return redisBlacklistService.isBlacklisted(
                jti
        );
    }
}