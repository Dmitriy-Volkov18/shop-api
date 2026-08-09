package com.example.shopapi.auth.services;

import com.example.shopapi.auth.dto.AuthResponse;
import com.example.shopapi.auth.dto.RefreshTokenClaims;
import com.example.shopapi.auth.dto.RiskResult;
import com.example.shopapi.auth.dto.SessionMeta;
import com.example.shopapi.auth.entities.RefreshToken;
import com.example.shopapi.auth.enums.RateLimitType;
import com.example.shopapi.auth.enums.SecurityDecision;
import com.example.shopapi.common.exception.runtimeExceptions.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RefreshSecurityService {

    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;
    private final TokenPolicyValidator tokenPolicyValidator;
    private final AdaptiveRateLimitService adaptiveRateLimitService;
    private final AuthService authService;
    private final RiskEngine riskEngine;

    @Transactional
    public AuthResponse refresh(
            String refreshToken,
            SessionMeta meta
    ) {
        RefreshToken stored = refreshTokenService.findByToken(refreshToken);
        RefreshTokenClaims claims = jwtService.extractRefreshClaims(refreshToken);

        tokenPolicyValidator.validate(
                stored,
                claims,
                meta
        );

        RiskResult risk = riskEngine.evaluateRefresh(stored, meta);

        if (risk.decision() == SecurityDecision.BLOCK) {
            throw new BadRequestException("Suspicious refresh detected");
        }

        if (risk.decision() == SecurityDecision.STEP_UP_AUTH) {
            throw new BadRequestException("Step-up authentication required");
        }

        adaptiveRateLimitService.check(
                meta.ip(),
                claims.deviceId(),
                RateLimitType.REFRESH,
                risk.level()
        );

        refreshTokenService.consume(stored);

        return authService.rotateRefreshSession(
                stored,
                meta,
                risk.level()
        );
    }
}