package com.example.shopapi.auth.services;

import com.example.shopapi.auth.dto.RefreshTokenClaims;
import com.example.shopapi.auth.dto.SessionMeta;
import com.example.shopapi.auth.entities.RefreshToken;
import com.example.shopapi.common.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class TokenPolicyValidator {

    private final RefreshTokenService refreshTokenService;
    private final DeviceFingerprintService deviceFingerprintService;

    public void validate(
            RefreshToken stored,
            RefreshTokenClaims claims,
            SessionMeta meta
    ) {

        if (stored.isRevoked()) {
            compromise(stored, "Refresh token reuse detected");
        }

        // 2. FAMILY BINDING
        if (!stored.getFamilyId().equals(claims.familyId())) {
            compromise(stored, "Family mismatch detected");
        }

        if (!stored.getJti().equals(claims.jti())) {
            compromise(stored, "JTI mismatch detected");
        }

        if (stored.getDeviceIdentity() == null) {
            compromise(stored, "Missing device identity");
        }

        if (!stored.getDeviceIdentity().getDeviceId()
                .equals(claims.deviceId())) {
            compromise(stored, "Device mismatch detected");
        }

        String currentFingerprint =
                deviceFingerprintService.fingerprint(meta.deviceInfo());

        String storedFingerprint =
                stored.getDeviceIdentity().getFingerprint();

        if (!storedFingerprint.equals(currentFingerprint)) {
            compromise(stored, "Device fingerprint mismatch (possible token theft)");
        }

        if (stored.getExpiryDate().isAfter(Instant.now())) {
            refreshTokenService.revokeFamily(stored.getFamilyId());
            throw new BadRequestException("Refresh token expired");
        }
    }

    private void compromise(
            RefreshToken token,
            String message
    ) {
        refreshTokenService.revokeFamily(token.getFamilyId());

        throw new BadRequestException(message);
    }
}