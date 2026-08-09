package com.example.shopapi.auth;

import com.example.shopapi.auth.dto.AuthResponse;
import com.example.shopapi.auth.dto.RegisterRequest;
import com.example.shopapi.auth.dto.SessionMeta;
import com.example.shopapi.auth.entities.RefreshToken;
import com.example.shopapi.auth.enums.RiskLevel;
import com.example.shopapi.auth.repositories.RefreshTokenRepository;
import com.example.shopapi.auth.services.AuthService;
import com.example.shopapi.auth.services.MailService;
import com.example.shopapi.testconfig.IntegrationTest;
import com.example.shopapi.testdata.TestDataFactory;
import com.example.shopapi.user.entities.User;
import com.example.shopapi.user.enums.UserRole;
import com.example.shopapi.user.repositories.UserRepository;
import com.example.shopapi.auth.services.SecurityAuditService;
import com.example.shopapi.common.exception.conflictExceptions.ConflictException;
import com.example.shopapi.auth.dto.SecurityAuditEvent;
import com.example.shopapi.auth.enums.SecurityEventType;
import com.example.shopapi.auth.services.RiskEngine;
import com.example.shopapi.auth.enums.SecurityDecision;
import com.example.shopapi.auth.dto.RiskResult;
import com.example.shopapi.auth.dto.LoginRequest;
import com.example.shopapi.common.exception.runtimeExceptions.BadRequestException;
import com.example.shopapi.auth.services.RefreshTokenService;
import com.example.shopapi.auth.services.UserAgentParser;
import com.example.shopapi.auth.services.JwtService;
import com.example.shopapi.auth.entities.DeviceInfo;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import org.mockito.ArgumentCaptor;


class AuthServiceTest extends IntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private MailService mailService;

    @MockitoBean
    private SecurityAuditService auditService;

    @MockitoBean
    private RiskEngine riskEngine;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private UserAgentParser userAgentParser;

    @Test
    void should_register_user_successfully() {
        RegisterRequest request = TestDataFactory.validRegisterRequest();
        SessionMeta meta = TestDataFactory.validSessionMeta();

        AuthResponse response =
                authService.register(
                        request,
                        meta,
                        RiskLevel.LOW
                );

        // Assert response

        assertThat(response)
                .isNotNull();

        assertThat(response.accessToken())
                .isNotBlank();

        assertThat(response.refreshToken())
                .isNotBlank();

        assertThat(response.deviceId())
                .isNotBlank();


        // Assert user

        User user =
                getUser(request.username());

        assertThat(user.getUsername())
                .isEqualTo(request.username());

        assertThat(user.getEmail())
                .isEqualTo(request.email());

        assertThat(user.getRole())
                .isEqualTo(UserRole.USER);

        assertThat(
                passwordEncoder.matches(
                        request.password(),
                        user.getPassword()
                )
        ).isTrue();


        // Assert refresh token

        RefreshToken token =
                getSingleRefreshToken(user);

        assertThat(token.getUser())
                .isEqualTo(user);

        assertThat(token.getFamilyId())
                .isNotBlank();

        assertThat(token.getJti())
                .isNotBlank();

        assertThat(token.getTokenHash())
                .isNotBlank();

        assertThat(token.getTokenHash())
                .isNotEqualTo(response.refreshToken());

        assertThat(token.isRevoked())
                .isFalse();

        assertThat(token.getExpiryDate())
                .isAfter(Instant.now());

        assertThat(token.getCreatedAt())
                .isNotNull();

        assertThat(token.getLastUsedAt())
                .isNotNull();

        assertThat(token.getIpAddress())
                .isEqualTo(meta.ip());

        assertThat(token.getCountry())
                .isEqualTo(meta.country());

        assertThat(token.getUserAgent())
                .isEqualTo(meta.userAgent());

        assertThat(token.isTrusted())
                .isFalse();

        assertThat(token.getNickname())
                .isNull();

        assertThat(token.getDeviceIdentity())
                .isNotNull();

        assertThat(token.getDeviceIdentity().getDeviceId())
                .isEqualTo(response.deviceId());

        assertThat(token.getDeviceIdentity().getFingerprint())
                .isNotBlank();

        assertThat(token.getDeviceIdentity().getDeviceInfo())
                .isNotNull();

        assertThat(token.getDeviceIdentity().getDeviceInfo().getBrowser())
                .isNotBlank();

        assertThat(token.getDeviceIdentity().getDeviceInfo().getOperatingSystem())
                .isEqualTo("Windows");


        assertSuccessfulAudit(
                user,
                response,
                token,
                meta,
                RiskLevel.LOW,
                SecurityEventType.REGISTER
        );

        // Assert mail
        verify(mailService)
                .send(
                        eq(request.email()),
                        eq("Verify your email"),
                        anyString()
                );
    }

    @Test
    void should_throw_when_username_already_exists() {
        RegisterRequest request = TestDataFactory.validRegisterRequest();
        SessionMeta meta = TestDataFactory.validSessionMeta();

        AuthResponse response =
                authService.register(
                        request,
                        meta,
                        RiskLevel.LOW
                );

        assertThatThrownBy(() ->
                authService.register(
                        request,
                        meta,
                        RiskLevel.LOW
                )
        )
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Username already exists");

        assertThat(
                userRepository.findAll()
        )
                .hasSize(1);

        User user = getUser(request.username());

        RefreshToken token = getSingleRefreshToken(user);

        assertThat(refreshTokenRepository.findAll())
                .hasSize(1);

        verify(mailService, times(1))
                .send(
                        eq(request.email()),
                        anyString(),
                        anyString()
                );

        assertSuccessfulAudit(
                user,
                response,
                token,
                meta,
                RiskLevel.LOW,
                SecurityEventType.REGISTER
        );
    }

    @Test
    void should_throw_when_email_already_exists() {
        RegisterRequest request1 = TestDataFactory.validRegisterRequest();
        RegisterRequest request2 = TestDataFactory.validRegisterRequest()
                .withUsername("anotherUser")
                .withEmail(request1.email());

        SessionMeta meta = TestDataFactory.validSessionMeta();

        AuthResponse response =
                authService.register(
                        request1,
                        meta,
                        RiskLevel.LOW
                );

        User user = getUser(request1.username());
        RefreshToken token = getSingleRefreshToken(user);

        assertThatThrownBy(() ->
                authService.register(
                        request2,
                        meta,
                        RiskLevel.LOW
                )
        )
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Email already exists");

        assertThat(
                userRepository.findByUsername("anotherUser")
        )
                .isEmpty();

        assertThat(
                refreshTokenRepository.findAll()
        )
                .hasSize(1);

        verify(mailService, times(1))
                .send(
                        eq(request1.email()),
                        anyString(),
                        anyString()
                );


        assertSuccessfulAudit(
                user,
                response,
                token,
                meta,
                RiskLevel.LOW,
                SecurityEventType.REGISTER
        );
    }

    @Test
    void should_register_user_with_high_risk_level() {
        RegisterRequest request = TestDataFactory.validRegisterRequest();
        SessionMeta meta = TestDataFactory.validSessionMeta();

        AuthResponse response = authService.register(
                request,
                meta,
                RiskLevel.HIGH
        );

        User user = getUser(request.username());
        RefreshToken token = getSingleRefreshToken(user);

        assertSuccessfulAudit(
                user,
                response,
                token,
                meta,
                RiskLevel.HIGH,
                SecurityEventType.REGISTER
        );
    }



    private User getUser(String username) {
        return userRepository
                .findByUsername(username)
                .orElseThrow();
    }

    private RefreshToken getSingleRefreshToken(User user) {
        List<RefreshToken> tokens = refreshTokenRepository.findAllByUserId(user.getId());

        assertThat(tokens)
                .hasSize(1);

        return tokens.getFirst();
    }

    private void assertSuccessfulAudit(
            User user,
            AuthResponse response,
            RefreshToken token,
            SessionMeta meta,
            RiskLevel riskLevel,
            SecurityEventType eventType
    ) {
        ArgumentCaptor<SecurityAuditEvent> captor =
                ArgumentCaptor.forClass(SecurityAuditEvent.class);

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

        assertThat(event.riskLevel())
                .isEqualTo(riskLevel);

        assertThat(event.deviceId())
                .isEqualTo(response.deviceId());

        assertThat(event.jti())
                .isEqualTo(token.getJti());

        assertThat(event.meta())
                .isEqualTo(meta);

        assertThat(event.failureReason())
                .isEmpty();
    }

    private void assertFailedAudit(
            User user,
            SessionMeta meta,
            RiskLevel riskLevel,
            SecurityEventType eventType,
            String failureReason
    ) {
        ArgumentCaptor<SecurityAuditEvent> captor =
                ArgumentCaptor.forClass(SecurityAuditEvent.class);

        verify(auditService)
                .log(captor.capture());

        SecurityAuditEvent event =
                captor.getValue();

        assertThat(event.eventType())
                .isEqualTo(eventType);

        assertThat(event.user())
                .isEqualTo(user);

        assertThat(event.success())
                .isFalse();

        assertThat(event.riskLevel())
                .isEqualTo(riskLevel);

        assertThat(event.meta())
                .isEqualTo(meta);

        assertThat(event.failureReason())
                .isEqualTo(failureReason);
    }


    private User saveValidUser() {
        User user =
                TestDataFactory.validUser(
                        passwordEncoder
                );

        return userRepository.save(user);
    }

    @Test
    void should_login_user_successfully() {
        User user = saveValidUser();
        LoginRequest request = TestDataFactory.validLoginRequest();
        SessionMeta meta = TestDataFactory.validSessionMeta();

        when(
                riskEngine.evaluateLogin(
                        eq(user),
                        anyList(),
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

        AuthResponse response = authService.login(request, meta);

        assertThat(response)
                .isNotNull();

        assertThat(response.accessToken())
                .isNotBlank();

        assertThat(response.refreshToken())
                .isNotBlank();

        assertThat(response.deviceId())
                .isNotBlank();

        User updated =
                getUser(
                        request.username()
                );

        assertThat(updated.getLastLoginAt())
                .isNotNull();

        assertThat(updated.getLastActivityAt())
                .isNotNull();

        RefreshToken token =
                getSingleRefreshToken(updated);

        assertThat(token.getUser())
                .isEqualTo(updated);

        assertThat(token.isRevoked())
                .isFalse();


        assertSuccessfulAudit(
                user,
                response,
                token,
                meta,
                RiskLevel.LOW,
                SecurityEventType.LOGIN
        );

        verify(mailService, never())
                .send(
                        anyString(),
                        anyString(),
                        anyString()
                );
    }

    @Test
    void should_fail_login_when_password_invalid() {
        User user = saveValidUser();

        LoginRequest request = TestDataFactory.validLoginRequest().withPassword("WrongPassword123!");
        SessionMeta meta = TestDataFactory.validSessionMeta();

        assertThatThrownBy(() ->
                authService.login(
                        request,
                        meta
                )
        )
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining(
                        "Invalid username or password"
                );

        assertFailedAudit(
                user,
                meta,
                RiskLevel.LOW,
                SecurityEventType.LOGIN_FAILED,
                "BAD_CREDENTIALS"
        );
    }

    @Test
    void should_block_login_when_email_not_verified() {
        User user = saveValidUser();
        user.setEmailVerified(false);

        userRepository.save(user);

        LoginRequest request = TestDataFactory.validLoginRequest();
        SessionMeta meta = TestDataFactory.validSessionMeta();

        assertThatThrownBy(() ->
                authService.login(
                        request,
                        meta
                )
        )
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining(
                        "Email verification required"
                );


        assertFailedAudit(
                user,
                meta,
                RiskLevel.LOW,
                SecurityEventType.LOGIN_FAILED,
                "EMAIL_NOT_VERIFIED"
        );
    }

    @Test
    void should_block_login_when_risk_engine_blocks() {
        User user = saveValidUser();
        LoginRequest request = TestDataFactory.validLoginRequest();
        SessionMeta meta = TestDataFactory.validSessionMeta();

        when(
                riskEngine.evaluateLogin(
                        eq(user),
                        anyList(),
                        eq(meta)
                )
        )
                .thenReturn(
                        new RiskResult(
                                100,
                                RiskLevel.HIGH,
                                SecurityDecision.BLOCK
                        )
                );

        assertThatThrownBy(() ->
                authService.login(
                        request,
                        meta
                )
        )
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining(
                        "Suspicious login detected"
                );

        assertFailedAudit(
                user,
                meta,
                RiskLevel.HIGH,
                SecurityEventType.LOGIN_FAILED,
                "BLOCKED"
        );
    }

    @Test
    void should_require_step_up_authentication_when_risk_is_high() {
        User user = saveValidUser();
        LoginRequest request = TestDataFactory.validLoginRequest();
        SessionMeta meta = TestDataFactory.validSessionMeta();

        when(
                riskEngine.evaluateLogin(
                        eq(user),
                        anyList(),
                        eq(meta)
                )
        )
                .thenReturn(
                        new RiskResult(
                                80,
                                RiskLevel.HIGH,
                                SecurityDecision.STEP_UP_AUTH
                        )
                );

        assertThatThrownBy(() ->
                authService.login(
                        request,
                        meta
                )
        )
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining(
                        "Step-up authentication required"
                );


        assertFailedAudit(
                user,
                meta,
                RiskLevel.HIGH,
                SecurityEventType.LOGIN_FAILED,
                "STEP_UP_REQUIRED"
        );
    }

    @Test
    void should_rotate_refresh_session_successfully() {
        User user = saveValidUser();
        SessionMeta meta = TestDataFactory.validSessionMeta();

        String deviceId = UUID.randomUUID().toString();
        String familyId = UUID.randomUUID().toString();

        RefreshToken oldToken =
                createRefreshSession(
                        user,
                        meta,
                        deviceId,
                        familyId
                );

        String oldJti = oldToken.getJti();

        AuthResponse response =
                authService.rotateRefreshSession(
                        oldToken,
                        meta,
                        RiskLevel.LOW
                );

        // Assert response

        assertThat(response)
                .isNotNull();

        assertThat(response.accessToken())
                .isNotBlank();

        assertThat(response.refreshToken())
                .isNotBlank();

        assertThat(response.deviceId())
                .isEqualTo(deviceId);

        // Assert refresh tokens

        List<RefreshToken> tokens =
                refreshTokenRepository
                        .findAllByUserId(user.getId());

        assertThat(tokens)
                .hasSize(2);

        RefreshToken newToken =
                tokens.stream()
                        .filter(token ->
                                !token.getJti().equals(oldJti)
                        )
                        .findFirst()
                        .orElseThrow();

        assertThat(newToken.isRevoked())
                .isFalse();

        assertThat(newToken.getUser())
                .isEqualTo(user);

        assertThat(newToken.getFamilyId())
                .isEqualTo(familyId);

        assertThat(newToken.getDeviceIdentity())
                .isNotNull();

        assertThat(newToken.getDeviceIdentity().getDeviceId())
                .isEqualTo(deviceId);

        assertThat(newToken.getDeviceIdentity().getDeviceInfo())
                .isNotNull();

        assertThat(newToken.getDeviceIdentity()
                .getDeviceInfo()
                .getBrowser())
                .isNotBlank();

        assertThat(newToken.getDeviceIdentity()
                .getDeviceInfo()
                .getOperatingSystem())
                .isEqualTo("Windows");

        assertThat(newToken.getJti())
                .isNotEqualTo(oldJti);

        assertThat(newToken.getTokenHash())
                .isNotBlank();

        assertThat(newToken.getExpiryDate())
                .isAfter(Instant.now());

        assertThat(newToken.getCreatedAt())
                .isNotNull();

        // Assert audit

        assertSuccessfulAudit(
                user,
                response,
                newToken,
                meta,
                RiskLevel.LOW,
                SecurityEventType.REFRESH
        );
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
                .findAllByUserId(user.getId())
                .getFirst();
    }
}