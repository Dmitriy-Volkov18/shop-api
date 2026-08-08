package com.example.shopapi.auth.dto;

import com.example.shopapi.auth.enums.RiskLevel;
import com.example.shopapi.auth.enums.SecurityDecision;

public record RiskResult(
        int score,
        RiskLevel level,
        SecurityDecision decision
) {}