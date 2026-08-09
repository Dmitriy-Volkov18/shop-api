package com.example.shopapi.auth;

import com.example.shopapi.auth.entities.DeviceInfo;
import com.example.shopapi.auth.entities.RefreshToken;
import com.example.shopapi.user.entities.User;
import com.example.shopapi.auth.enums.SecurityEventType;
import com.example.shopapi.auth.services.CurrentUserService;
import com.example.shopapi.auth.services.JwtService;
import com.example.shopapi.auth.services.RefreshTokenService;
import com.example.shopapi.auth.services.TokenBlacklistService;
import com.example.shopapi.auth.services.UserSessionService;
import com.example.shopapi.auth.services.UserAgentParser;
import com.example.shopapi.auth.dto.SecurityAuditEvent;
import com.example.shopapi.auth.services.SecurityAuditService;
import com.example.shopapi.auth.dto.SessionMeta;
import com.example.shopapi.user.enums.UserRole;
import com.example.shopapi.testconfig.IntegrationTest;
import com.example.shopapi.user.repositories.UserRepository;
import com.example.shopapi.auth.repositories.RefreshTokenRepository;
import com.example.shopapi.testdata.TestDataFactory;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mockito.ArgumentCaptor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;


class LogoutTest extends IntegrationTest {

    @Autowired
    private UserSessionService userSessionService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private CurrentUserService currentUserService;

    @MockitoBean
    private TokenBlacklistService tokenBlacklistService;

    @MockitoBean
    private SecurityAuditService auditService;

    @Autowired
    private UserAgentParser userAgentParser;

    @Test
    void should_logout_current_session_successfully() {
        User user = saveValidUser();
        SessionMeta meta = TestDataFactory.validSessionMeta();

        String refreshToken =
                createValidRefreshToken(
                        user,
                        meta
                );

        RefreshToken session =
                refreshTokenService.findByToken(
                        refreshToken
                );

        String accessToken = jwtService.generateAccessToken(user);
        String accessJti = jwtService.extractJti(accessToken);

        String authorizationHeader = "Bearer " + accessToken;

        when(currentUserService.getCurrentUserEntity())
                .thenReturn(user);

        userSessionService.logoutCurrentSession(
                refreshToken,
                authorizationHeader,
                meta
        );

        RefreshToken revoked =
                refreshTokenRepository
                        .findById(session.getId())
                        .orElseThrow();

        assertThat(revoked.isRevoked())
                .isTrue();

        verify(tokenBlacklistService)
                .revoke(accessJti);

        assertLogoutAudit(
                user,
                meta,
                SecurityEventType.LOGOUT_CURRENT,
                session.getDeviceIdentity()
                        .getDeviceId(),
                session.getJti(),
                "Current session logout"
        );
    }

    @Test
    void should_reject_logout_current_session_when_session_belongs_to_another_user() {
        User sessionOwner = saveValidUser();

        User currentUser = new User();
        currentUser.setUsername("anotherUser");
        currentUser.setEmail("another@test.com");
        currentUser.setPassword(
                passwordEncoder.encode("StrongPassword123!")
        );
        currentUser.setRole(UserRole.USER);
        currentUser.setEmailVerified(true);
        currentUser = userRepository.save(currentUser);

        SessionMeta meta = TestDataFactory.validSessionMeta();

        String refreshToken =
                createValidRefreshToken(
                        sessionOwner,
                        meta
                );

        RefreshToken session =
                refreshTokenService.findByToken(
                        refreshToken
                );

        String accessToken =
                jwtService.generateAccessToken(
                        sessionOwner
                );

        String authorizationHeader = "Bearer " + accessToken;

        when(currentUserService.getCurrentUserEntity())
                .thenReturn(currentUser);

        assertThatThrownBy(() ->
                userSessionService.logoutCurrentSession(
                        refreshToken,
                        authorizationHeader,
                        meta
                )
        )
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Not same user");

        RefreshToken unchanged =
                refreshTokenRepository
                        .findById(session.getId())
                        .orElseThrow();

        assertThat(unchanged.isRevoked())
                .isFalse();

        verify(tokenBlacklistService, never())
                .revoke(anyString());

        verify(auditService, never())
                .log(any(SecurityAuditEvent.class));
    }


    @Test
    void should_logout_all_devices_successfully() {
        User user = saveValidUser();
        SessionMeta meta = TestDataFactory.validSessionMeta();

        createRefreshSession(
                user,
                meta,
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString()
        );

        createRefreshSession(
                user,
                meta,
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString()
        );

        long oldTokenVersion = user.getTokenVersion();

        when(currentUserService.getCurrentUserEntity())
                .thenReturn(user);

        userSessionService.logoutAllDevices( meta);

        User updatedUser =
                getUser(
                        user.getUsername()
                );

        assertThat(updatedUser.getTokenVersion())
                .isEqualTo(oldTokenVersion + 1);

        refreshTokenRepository.flush();

        List<RefreshToken> tokens =
                refreshTokenRepository
                        .findAllByUserId(
                                user.getId()
                        );

        assertThat(tokens)
                .hasSize(2);

        assertThat(tokens)
                .allMatch(RefreshToken::isRevoked);


        // Assert audit

        ArgumentCaptor<SecurityAuditEvent> captor =
                ArgumentCaptor.forClass(
                        SecurityAuditEvent.class
                );

        verify(auditService)
                .log(captor.capture());

        SecurityAuditEvent event =
                captor.getValue();

        assertThat(event.eventType())
                .isEqualTo(
                        SecurityEventType.LOGOUT_ALL
                );

        assertThat(event.user())
                .isEqualTo(user);

        assertThat(event.success())
                .isTrue();

        assertThat(event.deviceId())
                .isNull();

        assertThat(event.jti())
                .isNull();

        assertThat(event.meta())
                .isEqualTo(meta);

        assertThat(event.failureReason())
                .isEmpty();
    }


    @Test
    void should_logout_single_session_successfully() {
        User user = saveValidUser();
        SessionMeta meta = TestDataFactory.validSessionMeta();

        RefreshToken sessionToLogout =
                createRefreshSession(
                        user,
                        meta,
                        UUID.randomUUID().toString(),
                        UUID.randomUUID().toString()
                );

        RefreshToken sessionToKeep =
                createRefreshSession(
                        user,
                        meta,
                        UUID.randomUUID().toString(),
                        UUID.randomUUID().toString()
                );

        String jti = sessionToLogout.getJti();

        userSessionService.logoutSession(
                jti,
                meta
        );

        // Assert target session

        RefreshToken revoked =
                refreshTokenRepository
                        .findById(
                                sessionToLogout.getId()
                        )
                        .orElseThrow();

        assertThat(revoked.isRevoked())
                .isTrue();


        // Assert other session remains active

        RefreshToken active =
                refreshTokenRepository
                        .findById(
                                sessionToKeep.getId()
                        )
                        .orElseThrow();

        assertThat(active.isRevoked())
                .isFalse();


        // Assert audit

        assertLogoutAudit(
                user,
                meta,
                SecurityEventType.LOGOUT_SESSION,
                sessionToLogout
                        .getDeviceIdentity()
                        .getDeviceId(),
                jti,
                "Single session logout"
        );
    }

    @Test
    void should_logout_all_except_current_session_successfully() {
        User user = saveValidUser();
        SessionMeta meta = TestDataFactory.validSessionMeta();

        RefreshToken currentSession =
                createRefreshSession(
                        user,
                        meta,
                        UUID.randomUUID().toString(),
                        UUID.randomUUID().toString()
                );

        RefreshToken otherSession =
                createRefreshSession(
                        user,
                        meta,
                        UUID.randomUUID().toString(),
                        UUID.randomUUID().toString()
                );

        String currentJti = currentSession.getJti();

        when(currentUserService.getCurrentUserEntity())
                .thenReturn(user);

        userSessionService.logoutAllExcept(
                currentJti,
                meta
        );

        // Assert current session

        RefreshToken current =
                refreshTokenRepository
                        .findById(currentSession.getId())
                        .orElseThrow();

        assertThat(current.isRevoked())
                .isFalse();


        // Assert other session

        RefreshToken other =
                refreshTokenRepository
                        .findById(otherSession.getId())
                        .orElseThrow();

        assertThat(other.isRevoked())
                .isTrue();


        // Assert audit

        assertLogoutAudit(
                user,
                meta,
                SecurityEventType.LOGOUT_ALL_EXCEPT,
                currentSession
                        .getDeviceIdentity()
                        .getDeviceId(),
                currentJti,
                "All sessions except current were logged out"
        );
    }


    private void assertLogoutAudit(
            User user,
            SessionMeta meta,
            SecurityEventType eventType,
            String deviceId,
            String jti,
            String message
    ) {
        ArgumentCaptor<SecurityAuditEvent> captor =
                ArgumentCaptor.forClass(
                        SecurityAuditEvent.class
                );

        verify(auditService)
                .log(captor.capture());

        SecurityAuditEvent event =
                captor.getValue();

        assertThat(event.eventType())
                .isEqualTo(eventType);

        assertThat(event.user())
                .isEqualTo(user);

        assertThat(event.success())
                .isTrue();

        assertThat(event.deviceId())
                .isEqualTo(deviceId);

        assertThat(event.jti())
                .isEqualTo(jti);

        assertThat(event.details())
                .isEqualTo(message);

        assertThat(event.meta())
                .isEqualTo(meta);

        assertThat(event.riskLevel())
                .isNull();

        assertThat(event.failureReason())
                .isEmpty();
    }


    private User getUser(String username) {
        return userRepository
                .findByUsername(username)
                .orElseThrow();
    }


    private User saveValidUser() {
        User user =
                TestDataFactory.validUser(
                        passwordEncoder()
                );

        return userRepository.save(user);
    }


    private String createValidRefreshToken(
            User user,
            SessionMeta meta
    ) {
        String deviceId = UUID.randomUUID().toString();
        String familyId = UUID.randomUUID().toString();

        String refreshToken =
                jwtService.generateRefreshToken(
                        user,
                        deviceId,
                        familyId
                );

        String jti =
                jwtService.extractJti(
                        refreshToken
                );

        DeviceInfo deviceInfo =
                userAgentParser.parse(
                        meta.userAgent()
                );

        refreshTokenService.createSession(
                user,
                refreshToken,
                deviceId,
                meta.userAgent(),
                deviceInfo,
                meta.ip(),
                meta.country(),
                jti,
                familyId
        );

        return refreshToken;
    }


    private RefreshToken createRefreshSession(
            User user,
            SessionMeta meta,
            String deviceId,
            String familyId
    ) {
        String refreshToken =
                jwtService.generateRefreshToken(
                        user,
                        deviceId,
                        familyId
                );

        String jti = jwtService.extractJti(refreshToken);

        DeviceInfo deviceInfo =
                userAgentParser.parse(
                        meta.userAgent()
                );

        refreshTokenService.createSession(
                user,
                refreshToken,
                deviceId,
                meta.userAgent(),
                deviceInfo,
                meta.ip(),
                meta.country(),
                jti,
                familyId
        );

        return refreshTokenRepository
                .findAllByUserId(
                        user.getId()
                )
                .stream()
                .filter(token ->
                        token.getJti().equals(jti)
                )
                .findFirst()
                .orElseThrow();
    }


    private PasswordEncoder passwordEncoder() {
        return applicationContext
                .getBean(PasswordEncoder.class);
    }

    @Autowired
    private org.springframework.context.ApplicationContext applicationContext;

}