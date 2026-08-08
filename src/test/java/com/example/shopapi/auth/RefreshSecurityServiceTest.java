package com.example.shopapi.auth;

import com.example.shopapi.auth.dto.AuthResponse;
import com.example.shopapi.auth.dto.RefreshTokenClaims;
import com.example.shopapi.auth.dto.SecurityAuditEvent;
import com.example.shopapi.auth.dto.SessionMeta;
import com.example.shopapi.auth.entities.RefreshToken;
import com.example.shopapi.auth.enums.RiskLevel;
import com.example.shopapi.auth.enums.SecurityDecision;
import com.example.shopapi.auth.enums.SecurityEventType;
import com.example.shopapi.auth.repositories.RefreshTokenRepository;
import com.example.shopapi.auth.services.RefreshSecurityService;
import com.example.shopapi.auth.services.SecurityAuditService;
import com.example.shopapi.auth.services.RefreshTokenService;
import com.example.shopapi.auth.services.RiskEngine;
import com.example.shopapi.auth.services.MailService;
import com.example.shopapi.testconfig.IntegrationTest;
import com.example.shopapi.testdata.TestDataFactory;
import com.example.shopapi.user.entities.User;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import com.example.shopapi.user.repositories.UserRepository;
import java.time.Instant;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.shopapi.auth.dto.RiskResult;
import com.example.shopapi.auth.dto.DeviceInfo;
import com.example.shopapi.auth.services.UserAgentParser;
import com.example.shopapi.auth.services.JwtService;
import java.util.UUID;
import com.example.shopapi.auth.services.AuthService;
import com.example.shopapi.common.exception.BadRequestException;
import com.example.shopapi.auth.services.AdaptiveRateLimitService;
import com.example.shopapi.auth.services.TokenPolicyValidator;
import com.example.shopapi.auth.enums.RateLimitType;
import com.example.shopapi.auth.repositories.RefreshTokenRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.never;

class RefreshSecurityServiceTest extends IntegrationTest {

    @Autowired
    private RefreshSecurityService refreshSecurityService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @MockitoBean
    private TokenPolicyValidator tokenPolicyValidator;

    @MockitoBean
    private AdaptiveRateLimitService adaptiveRateLimitService;

    @MockitoBean
    private RiskEngine riskEngine;

    @MockitoBean
    private AuthService authService;

    @Autowired
    private UserAgentParser userAgentParser;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;


    private User saveValidUser() {

        User user =
                TestDataFactory.validUser(passwordEncoder);

        return userRepository.save(user);
    }

    private String createValidRefreshToken(
            User user,
            SessionMeta meta
    ) {

        String deviceId =
                UUID.randomUUID().toString();

        String familyId =
                UUID.randomUUID().toString();

        String refreshToken =
                jwtService.generateRefreshToken(
                        user,
                        deviceId,
                        familyId
                );

        String jti =
                jwtService.extractJti(refreshToken);

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

    @Test
    void should_refresh_successfully() {

        User user = saveValidUser();

        SessionMeta meta =
                TestDataFactory.validSessionMeta();

        String refreshToken =
                createValidRefreshToken(
                        user,
                        meta
                );

        RefreshToken oldToken =
                refreshTokenService.findByToken(
                        refreshToken
                );

        when(
                riskEngine.evaluateRefresh(
                        eq(oldToken),
                        eq(meta)
                )
        )
                .thenReturn(
                        new RiskResult(
                                0,
                                RiskLevel.LOW,
                                SecurityDecision.ALLOW
                        )
                );

        AuthResponse expectedResponse =
                new AuthResponse(
                        "access-token",
                        "new-refresh-token",
                        oldToken
                                .getDeviceIdentity()
                                .getDeviceId()
                );

        when(
                authService.rotateRefreshSession(
                        eq(oldToken),
                        eq(meta),
                        eq(RiskLevel.LOW)
                )
        )
                .thenReturn(expectedResponse);

        // Act

        AuthResponse response =
                refreshSecurityService.refresh(
                        refreshToken,
                        meta
                );

        // Assert response

        assertThat(response)
                .isNotNull();

        assertThat(response.getAccessToken())
                .isEqualTo("access-token");

        assertThat(response.getRefreshToken())
                .isEqualTo("new-refresh-token");

        assertThat(response.getRefreshToken())
                .isNotEqualTo(refreshToken);

        assertThat(response.getDeviceId())
                .isEqualTo(
                        oldToken
                                .getDeviceIdentity()
                                .getDeviceId()
                );

        // Assert old token was consumed

        RefreshToken consumedToken =
                refreshTokenRepository
                        .findById(oldToken.getId())
                        .orElseThrow();

        assertThat(consumedToken.isRevoked())
                .isTrue();

        // Assert risk evaluation

        verify(riskEngine)
                .evaluateRefresh(
                        eq(oldToken),
                        eq(meta)
                );

        // Assert rate limit

        verify(adaptiveRateLimitService)
                .check(
                        eq(meta.ip()),
                        eq(
                                oldToken
                                        .getDeviceIdentity()
                                        .getDeviceId()
                        ),
                        eq(RateLimitType.REFRESH),
                        eq(RiskLevel.LOW)
                );

        // Assert session rotation was delegated to AuthService

        verify(authService)
                .rotateRefreshSession(
                        eq(oldToken),
                        eq(meta),
                        eq(RiskLevel.LOW)
                );
    }

}
