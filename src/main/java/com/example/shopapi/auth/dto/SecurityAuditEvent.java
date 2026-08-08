package com.example.shopapi.auth.dto;

import com.example.shopapi.user.entities.User;
import com.example.shopapi.auth.enums.RiskLevel;
import com.example.shopapi.auth.enums.SecurityEventType;

public record SecurityAuditEvent(
        User user,
        SecurityEventType eventType,
        boolean success,

        String deviceId,
        String jti,

        String details,
        SessionMeta meta,

        RiskLevel riskLevel,
        String failureReason
) {}