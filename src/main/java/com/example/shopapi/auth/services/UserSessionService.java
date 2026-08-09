package com.example.shopapi.auth.services;

import com.example.shopapi.auth.dto.SecurityAuditEvent;
import com.example.shopapi.auth.dto.SessionMeta;
import com.example.shopapi.auth.entities.RefreshToken;
import com.example.shopapi.auth.enums.SecurityEventType;
import com.example.shopapi.user.entities.User;
import com.example.shopapi.user.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserSessionService {

    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;
    private final SecurityAuditService auditService;
    private final JwtService jwtService;
    private final TokenBlacklistService tokenBlacklistService;
    private final CurrentUserService currentUserService;

    @Transactional
    public void logoutCurrentSession(
            String refreshToken,
            String authorizationHeader,
            SessionMeta meta
    ) {
        RefreshToken session = refreshTokenService.findByToken(refreshToken);
        User user = session.getUser();
        User currentUser = currentUserService.getCurrentUserEntity();

        if(!user.equals(currentUser)){
            throw new AccessDeniedException("Not same user");
        }

        refreshTokenService.revoke(session);

        String accessToken = jwtService.extractAccessToken(authorizationHeader);
        String accessJti = jwtService.extractJti(accessToken);

        tokenBlacklistService.revoke(accessJti);

        auditService.log(
                new SecurityAuditEvent(
                        user,
                        SecurityEventType.LOGOUT_CURRENT,
                        true,
                        session.getDeviceIdentity().getDeviceId(),
                        session.getJti(),
                        "Current session logout",
                        meta,
                        null,
                        ""
                )
        );
    }

    @Transactional
    public void logoutAllDevices(SessionMeta meta) {
        User user = currentUserService.getCurrentUserEntity();
        user.setTokenVersion(user.getTokenVersion() + 1);
        userRepository.save(user);

        refreshTokenService.revokeAllByUserId(user.getId());

        auditService.log(
                new SecurityAuditEvent(
                        user,
                        SecurityEventType.LOGOUT_ALL,
                        true,
                        null,
                        null,
                        "All devices logged out",
                        meta,
                        null,
                        ""
                )
        );
    }

    @Transactional
    public void logoutSession(String jti, SessionMeta meta) {
        RefreshToken session = refreshTokenService.findByJti(jti);
        User user = session.getUser();
        String deviceId = session.getDeviceIdentity().getDeviceId();

        refreshTokenService.revoke(session);

        auditService.log(
                new SecurityAuditEvent(
                        user,
                        SecurityEventType.LOGOUT_SESSION,
                        true,
                        deviceId,
                        jti,
                        "Single session logout",
                        meta,
                        null,
                        ""
                )
        );
    }

    @Transactional
    public void logoutAllExcept(String currentJti, SessionMeta meta) {
        User user = currentUserService.getCurrentUserEntity();
        Long userId = user.getId();

        RefreshToken currentSession =
                getOwnedSession(
                        userId,
                        currentJti
                );

        refreshTokenService.revokeAllExcept(userId, currentJti);

        auditService.log(
                new SecurityAuditEvent(
                        user,
                        SecurityEventType.LOGOUT_ALL_EXCEPT,
                        true,
                        currentSession
                                .getDeviceIdentity()
                                .getDeviceId(),
                        currentJti,
                        "All sessions except current were logged out",
                        meta,
                        null,
                        ""
                )
        );
    }

    @Transactional
    public void trustSession(
            Long userId,
            String jti,
            SessionMeta meta
    ) {
        RefreshToken session = getOwnedSession(userId, jti);
        session.setTrusted(true);

        auditService.log(
                new SecurityAuditEvent(
                        session.getUser(),
                        SecurityEventType.SESSION_TRUSTED,
                        true,
                        session.getDeviceIdentity().getDeviceId(),
                        jti,
                        "Device marked as trusted",
                        meta,
                        null,
                        ""
                )
        );
    }

    @Transactional
    public void untrustSession(
            Long userId,
            String jti,
            SessionMeta meta
    ) {
        RefreshToken session = getOwnedSession(userId, jti);
        session.setTrusted(false);

        auditService.log(
                new SecurityAuditEvent(
                        session.getUser(),
                        SecurityEventType.SESSION_UNTRUSTED,
                        true,
                        session.getDeviceIdentity().getDeviceId(),
                        jti,
                        "Device trust removed",
                        meta,
                        null,
                        ""
                )
        );
    }

    @Transactional
    public void updateSessionNickname(
            Long userId,
            String jti,
            String nickname,
            SessionMeta meta
    ) {
        RefreshToken session = getOwnedSession(userId, jti);
        session.setTrusted(true);
        session.setNickname(nickname);

        auditService.log(
                new SecurityAuditEvent(
                        session.getUser(),
                        SecurityEventType.SESSION_RENAMED,
                        true,
                        session.getDeviceIdentity().getDeviceId(),
                        jti,
                        "Session nickname changed",
                        meta,
                        null,
                        ""
                )
        );
    }

    private RefreshToken getOwnedSession(
            Long userId,
            String jti
    ) {
        RefreshToken session = refreshTokenService.findByJti(jti);

        if (!session.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Access denied");
        }

        return session;
    }

    private User getUserOrThrow(
            Long userId
    ) {
        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User not found"
                        ));
    }
}