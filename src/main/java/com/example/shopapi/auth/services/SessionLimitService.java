package com.example.shopapi.auth.services;

import com.example.shopapi.auth.dto.SecurityAuditEvent;
import com.example.shopapi.auth.dto.SessionMeta;
import com.example.shopapi.auth.entities.RefreshToken;
import com.example.shopapi.user.entities.User;
import com.example.shopapi.auth.enums.SecurityEventType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SessionLimitService {

    private static final int MAX_SESSIONS = 5;

    private final RefreshTokenService refreshTokenService;
    private final SecurityAuditService auditService;

    public void enforce(User user, SessionMeta meta) {
        List<RefreshToken> sessions = refreshTokenService.getActiveSessionsOrdered(user.getId());

        if (sessions.size() < MAX_SESSIONS) {
            return;
        }

        RefreshToken removed =
                sessions.stream()
                        .filter(session -> !session.isTrusted())
                        .findFirst()
                        .orElse(sessions.getFirst());

        refreshTokenService.revoke(
                removed
        );

        auditService.log(
                new SecurityAuditEvent(
                        user,
                        SecurityEventType.SESSION_LIMIT_KICK,
                        true,
                        removed.getDeviceIdentity().getDeviceId(),
                        removed.getJti(),
                        "Session removed because device limit exceeded",
                        meta,
                        null,
                        ""
                )
        );
    }
}