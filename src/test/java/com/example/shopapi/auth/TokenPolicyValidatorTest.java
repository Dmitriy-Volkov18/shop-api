package com.example.shopapi.auth;

import com.example.shopapi.auth.dto.RefreshTokenClaims;
import com.example.shopapi.auth.entities.RefreshToken;
import com.example.shopapi.auth.services.DeviceFingerprintService;
import com.example.shopapi.auth.services.JwtService;
import com.example.shopapi.auth.services.RefreshTokenService;
import com.example.shopapi.auth.services.TokenPolicyValidator;
import com.example.shopapi.auth.dto.SessionMeta;
import com.example.shopapi.common.exception.BadRequestException;
import com.example.shopapi.user.entities.User;
import com.example.shopapi.auth.entities.DeviceInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import com.example.shopapi.testconfig.IntegrationTest;
import com.example.shopapi.user.repositories.UserRepository;
import com.example.shopapi.auth.repositories.RefreshTokenRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.shopapi.auth.services.UserAgentParser;
import com.example.shopapi.testdata.TestDataFactory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;

class TokenPolicyValidatorTest extends IntegrationTest {
    @Autowired
    private TokenPolicyValidator tokenPolicyValidator;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private JwtService jwtService;

    @MockitoBean
    private DeviceFingerprintService deviceFingerprintService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserAgentParser userAgentParser;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {

        when(
                deviceFingerprintService.fingerprint(
                        any(DeviceInfo.class)
                )
        )
                .thenReturn("test-fingerprint");
    }

    @Test
    void should_block_when_refresh_token_already_used() {

        User user =
                saveValidUser();

        SessionMeta meta =
                TestDataFactory.validSessionMeta();

        String refreshToken =
                createValidRefreshToken(
                        user,
                        meta
                );

        RefreshToken stored =
                refreshTokenService.findByToken(
                        refreshToken
                );

        // Имитируем повторное использование refresh token
        stored.setRevoked(true);

        refreshTokenRepository.save(stored);

        RefreshTokenClaims claims =
                jwtService.extractRefreshClaims(
                        refreshToken
                );

        assertThatThrownBy(() ->
                tokenPolicyValidator.validate(
                        stored,
                        claims,
                        meta
                )
        )
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining(
                        "Refresh token reuse detected"
                );

        RefreshToken revokedToken =
                refreshTokenRepository
                        .findById(stored.getId())
                        .orElseThrow();

        assertThat(revokedToken.isRevoked())
                .isTrue();
    }


    @Test
    void should_block_when_family_mismatch() {

        User user =
                saveValidUser();

        SessionMeta meta =
                TestDataFactory.validSessionMeta();

        String refreshToken =
                createValidRefreshToken(
                        user,
                        meta
                );

        RefreshToken stored =
                refreshTokenService.findByToken(
                        refreshToken
                );

        RefreshTokenClaims validClaims =
                jwtService.extractRefreshClaims(
                        refreshToken
                );

        RefreshTokenClaims invalidClaims =
                new RefreshTokenClaims(
                        validClaims.jti(),
                        validClaims.deviceId(),
                        UUID.randomUUID().toString()
                );

        assertThat(invalidClaims.familyId())
                .isNotEqualTo(
                        stored.getFamilyId()
                );

        assertThatThrownBy(() ->
                tokenPolicyValidator.validate(
                        stored,
                        invalidClaims,
                        meta
                )
        )
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining(
                        "Family mismatch detected"
                );

        entityManager.flush();
        entityManager.clear();

        RefreshToken revokedToken =
                refreshTokenRepository
                        .findById(stored.getId())
                        .orElseThrow();

        assertThat(revokedToken.isRevoked())
                .isTrue();
    }


    private User saveValidUser() {

        User user =
                TestDataFactory.validUser(
                        passwordEncoder
                );

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
                jwtService.extractJti(
                        refreshToken
                );

        DeviceInfo deviceInfo =
                userAgentParser.parse(
                        meta.userAgent()
                );

        String fingerprint =
                deviceFingerprintService.fingerprint(
                        meta.deviceInfo()
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

        RefreshToken token =
                refreshTokenRepository
                        .findByJti(jti)
                        .orElseThrow();

        token.getDeviceIdentity()
                .setFingerprint(fingerprint);

        refreshTokenRepository.save(token);

        return refreshToken;
    }
}