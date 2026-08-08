package com.example.shopapi.auth.services;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenCleanupJob {

    private final RefreshTokenService refreshTokenService;

    @Scheduled(
            fixedDelay = 3600000
    )
    public void cleanup() {
        refreshTokenService.cleanupExpired();
    }
}