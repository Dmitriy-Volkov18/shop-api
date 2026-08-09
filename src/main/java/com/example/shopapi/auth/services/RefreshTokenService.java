package com.example.shopapi.auth.services;

import com.example.shopapi.auth.entities.DeviceIdentity;
import com.example.shopapi.auth.entities.DeviceInfo;
import com.example.shopapi.auth.entities.RefreshToken;
import com.example.shopapi.user.entities.User;
import com.example.shopapi.common.exception.BadRequestException;
import com.example.shopapi.auth.repositories.RefreshTokenRepository;
import com.example.shopapi.common.config.JwtProperties;
import com.example.shopapi.auth.security.TokenHashUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository repository;
    private final JwtProperties jwtProperties;
    private final DeviceFingerprintService deviceFingerprintService;

    @Transactional
    public RefreshToken createSession(
            User user,
            String token,
            String deviceId,
            String userAgent,
            DeviceInfo deviceInfo,
            String ip,
            String country,
            String jti,
            String familyId
    ) {
        RefreshToken refreshToken = buildRefreshToken(
                user,
                token,
                deviceId,
                userAgent,
                deviceInfo,
                ip,
                country,
                jti,
                familyId
        );

        return repository.save(refreshToken);
    }

    private RefreshToken buildRefreshToken(
            User user,
            String token,
            String deviceId,
            String userAgent,
            DeviceInfo deviceInfo,
            String ip,
            String country,
            String jti,
            String familyId
    ) {

        Instant now = Instant.now();

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setTokenHash(TokenHashUtil.hash(token));
        refreshToken.setJti(jti);
        refreshToken.setFamilyId(familyId);

        DeviceIdentity identity = new DeviceIdentity();
        identity.setDeviceId(deviceId);
        identity.setDeviceInfo(deviceInfo);
        identity.setFingerprint(
                deviceFingerprintService.fingerprint(deviceInfo)
        );

        refreshToken.setDeviceIdentity(identity);
        refreshToken.setUserAgent(userAgent);
        refreshToken.setIpAddress(ip);
        refreshToken.setCountry(country);
        refreshToken.setCreatedAt(now);
        refreshToken.setLastUsedAt(now);
        refreshToken.setExpiryDate(
                now.plus(jwtProperties.getRefreshExpiration())
        );
        refreshToken.setRevoked(false);
        refreshToken.setTrusted(false);

        return refreshToken;
    }

    @Transactional
    public void revoke(RefreshToken token) {
        token.setRevoked(true);
        repository.save(token);
    }

    @Transactional(readOnly = true)
    public List<RefreshToken> getActiveSessions(Long userId) {
        return repository
                .findAllByUserIdAndRevokedFalseOrderByCreatedAtDesc(userId);
    }


    @Transactional(readOnly = true)
    public List<RefreshToken> getSessionHistory(Long userId) {
        return repository
                .findAllByUserIdOrderByCreatedAtDesc(userId);
    }


    public RefreshToken findByJti(String jti) {
        return repository.findByJti(jti)
                .orElseThrow(() ->
                        new BadRequestException("Session not found"));
    }

    public List<RefreshToken> getSessionsOrdered(Long userId) {
        return repository.findAllByUserIdOrderByCreatedAtAsc(userId);
    }

    @Transactional
    public void revokeFamily(String familyId) {
        repository.revokeAllByFamilyId(familyId);
    }

    public List<RefreshToken> findAllByUserId(Long userId) {
        return repository.findAllByUserId(userId);
    }

    public Optional<RefreshToken> findByTokenHash(String tokenHash) {
        return repository.findByTokenHash(tokenHash);
    }

    public RefreshToken findByToken(String refreshToken) {
        String hash = TokenHashUtil.hash(refreshToken);

        return findByTokenHash(hash)
                .orElseThrow(() ->
                        new BadRequestException("Invalid refresh token"));
    }

    @Transactional
    public void consume(RefreshToken token) {
        int updated = repository.consume(token.getId());

        if (updated != 1) {
            throw new BadRequestException(
                    "Refresh token has already been used."
            );
        }

        token.setRevoked(true);
    }

    @Transactional
    public void cleanupExpired() {
        repository.deleteExpired(Instant.now());
    }

    @Transactional
    public void revokeByJti(String jti){
        repository.revokeByJti(jti);
    }

    @Transactional
    public void revokeAllByUserId(Long userId){
        repository.revokeAllByUserId(userId);
    }

    @Transactional
    public void revokeAllExcept(Long userId, String currentJti){
        repository.revokeAllByUserIdExceptJti(userId, currentJti);
    }

    public List<RefreshToken> getActiveSessionsOrdered(Long userId) {
        return repository.findAllByUserIdAndRevokedFalseOrderByCreatedAtAsc(userId);
    }

    @Transactional
    public void deleteAllByUserId(Long userId) {
        repository.deleteAllByUserId(userId);
    }
}