package com.example.shopapi.common.infrastructure.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisTokenService {

    private final RedisService redisService;
    private final RedisKeyBuilder keyBuilder;


    public void saveEmailVerificationToken(
            String tokenHash,
            Long userId,
            Duration ttl
    ) {

        redisService.set(
                keyBuilder.emailVerification(tokenHash),
                userId,
                ttl
        );
    }


    public Long getEmailVerificationUserId(
            String tokenHash
    ) {

        return redisService.get(
                keyBuilder.emailVerification(tokenHash),
                Long.class
        );
    }



    public void deleteEmailVerificationToken(
            String tokenHash
    ) {

        redisService.delete(
                keyBuilder.emailVerification(tokenHash)
        );
    }



    public void savePasswordResetToken(
            String tokenHash,
            Long userId,
            Duration ttl
    ) {

        redisService.set(
                keyBuilder.passwordReset(tokenHash),
                userId,
                ttl
        );
    }



    public Long getPasswordResetUserId(
            String tokenHash
    ) {

        return redisService.get(
                keyBuilder.passwordReset(tokenHash),
                Long.class
        );
    }



    public void deletePasswordResetToken(
            String tokenHash
    ) {

        redisService.delete(
                keyBuilder.passwordReset(tokenHash)
        );
    }

    public void saveUserVerificationToken(
            Long userId,
            String tokenHash,
            Duration ttl
    ) {

        redisService.set(
                keyBuilder.emailVerificationByUser(userId),
                tokenHash,
                ttl
        );
    }

    public String getUserVerificationTokenHash(
            Long userId
    ) {

        return redisService.get(
                keyBuilder.emailVerificationByUser(userId),
                String.class
        );
    }

    public void deleteUserVerificationToken(
            Long userId
    ) {

        redisService.delete(
                keyBuilder.emailVerificationByUser(userId)
        );
    }

}