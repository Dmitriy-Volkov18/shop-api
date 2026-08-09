package com.example.shopapi.auth.services;

import com.example.shopapi.auth.dto.RiskResult;
import com.example.shopapi.auth.dto.SessionMeta;
import com.example.shopapi.auth.entities.RefreshToken;
import com.example.shopapi.user.entities.User;
import com.example.shopapi.auth.enums.RiskLevel;
import com.example.shopapi.auth.enums.SecurityDecision;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RiskEngine {

    private final SuspiciousLoginService suspiciousLoginService;

    public RiskResult evaluateLogin(User user,
                                    List<RefreshToken> sessions,
                                    SessionMeta meta) {

        int score = suspiciousLoginService.calculateRiskOnLogin(user, sessions, meta);
        RiskLevel level = map(score);
        SecurityDecision decision = decide(level);

        return new RiskResult(score, level, decision);
    }

    public RiskResult evaluateRefresh(RefreshToken token, SessionMeta meta) {
        int score = suspiciousLoginService.calculateRisk(token, meta);
        RiskLevel level = map(score);
        SecurityDecision decision = decide(level);

        return new RiskResult(score, level, decision);
    }

    private RiskLevel map(int score) {
        if (score <= 30) {
            return RiskLevel.LOW;
        }

        if (score <= 70) {
            return RiskLevel.MEDIUM;
        }

        return RiskLevel.HIGH;
    }

    private SecurityDecision decide(RiskLevel level) {
        return switch (level) {
            case LOW -> SecurityDecision.ALLOW;
            case MEDIUM -> SecurityDecision.STEP_UP_AUTH;
            case HIGH -> SecurityDecision.BLOCK;
        };
    }
}