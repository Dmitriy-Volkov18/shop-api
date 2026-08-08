package com.example.shopapi.common.infrastructure.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RedisSessionService {

    private final RedisService redisService;
    private final RedisKeyBuilder keyBuilder;

    public void addSession(
            Long userId,
            Long refreshTokenId
    ) {

        String key =
                keyBuilder.userSessions(userId);

        redisService.zAdd(
                key,
                refreshTokenId,
                System.currentTimeMillis()
        );

        redisService.expire(
                key,
                Duration.ofDays(30)
        );
    }

    public long count(
            Long userId
    ) {

        Long count =
                redisService.zCard(
                        keyBuilder.userSessions(userId)
                );

        return count == null
                ? 0
                : count;
    }

    public void removeSession(
            Long userId,
            Long refreshTokenId
    ) {

        redisService.zRemove(
                keyBuilder.userSessions(userId),
                refreshTokenId
        );
    }

    public List<Long> getSessionIds(
            Long userId
    ) {

        Set<Object> values =
                redisService.zRange(
                        keyBuilder.userSessions(userId),
                        0,
                        -1
                );

        if (values == null || values.isEmpty()) {
            return List.of();
        }

        return values.stream()
                .map(Long.class::cast)
                .toList();
    }

    public Long getOldestSessionId(
            Long userId
    ) {

        Set<Object> values =
                redisService.zRange(
                        keyBuilder.userSessions(userId),
                        0,
                        0
                );

        if (values == null || values.isEmpty()) {
            return null;
        }

        return (Long) values.iterator().next();
    }

    public void touchSession(
            Long userId,
            Long refreshTokenId
    ) {

        redisService.zAdd(
                keyBuilder.userSessions(userId),
                refreshTokenId,
                System.currentTimeMillis()
        );
    }
}