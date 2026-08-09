package com.example.shopapi.auth.services;

import com.example.shopapi.auth.dto.ChangePasswordRequest;
import com.example.shopapi.auth.dto.ForgotPasswordRequest;
import com.example.shopapi.auth.dto.ResetPasswordRequest;
import com.example.shopapi.auth.dto.SecurityAuditEvent;
import com.example.shopapi.auth.dto.SessionMeta;
import com.example.shopapi.auth.enums.RateLimitType;
import com.example.shopapi.auth.enums.RiskLevel;
import com.example.shopapi.auth.enums.SecurityEventType;
import com.example.shopapi.common.exception.runtimeExceptions.BadRequestException;
import com.example.shopapi.user.entities.User;
import com.example.shopapi.user.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PasswordManagementService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final PasswordResetService passwordResetService;
    private final AdaptiveRateLimitService adaptiveRateLimitService;
    private final SecurityAuditService auditService;
    private final MailService mailService;
    private final CurrentUserService currentUserService;

    private void updatePassword(User user, String rawPassword) {
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setTokenVersion(user.getTokenVersion() + 1);
        userRepository.save(user);
        refreshTokenService.revokeAllByUserId(user.getId());
    }

    @Transactional
    public void changePassword(
            ChangePasswordRequest request,
            SessionMeta meta
    ) {
        User user = currentUserService.getCurrentUserEntity();

        if (!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }

        if (passwordEncoder.matches(request.newPassword(), user.getPassword())) {
            throw new BadRequestException(
                    "New password must be different from the current password");
        }

        updatePassword(user, request.newPassword());

        auditService.log(
                new SecurityAuditEvent(
                        user,
                        SecurityEventType.PASSWORD_CHANGED,
                        true,
                        null,
                        null,
                        "Password successfully changed",
                        meta,
                        null,
                        ""
                )
        );
    }

    public void forgotPassword(
            ForgotPasswordRequest request,
            SessionMeta meta
    ) {
        adaptiveRateLimitService.check(
                meta.ip(),
                request.email(),
                RateLimitType.PASSWORD_RESET,
                RiskLevel.LOW
        );

        Optional<User> optionalUser = userRepository.findByEmail(request.email());

        if (optionalUser.isEmpty()) {
            return;
        }

        User user = optionalUser.get();
        String token = passwordResetService.createToken(user);

        mailService.send(
                user.getEmail(),
                "Password reset",
                """
                Hello!
        
                To reset your password open:
        
                http://localhost:8080/auth/reset-password?token=%s
        
                Link expires in 15 minutes.
                """.formatted(token)
        );

        auditService.log(
                new SecurityAuditEvent(
                        user,
                        SecurityEventType.PASSWORD_RESET_REQUESTED,
                        true,
                        null,
                        null,
                        "Password reset requested",
                        meta,
                        null,
                        ""
                )
        );
    }

    @Transactional
    public void resetPassword(
            ResetPasswordRequest request,
            SessionMeta meta
    ) {
        User user =
                passwordResetService.validateToken(
                        request.token()
                );

        if (passwordEncoder.matches(request.newPassword(), user.getPassword())) {
            throw new BadRequestException(
                    "New password must be different"
            );
        }

        updatePassword(user, request.newPassword());

        passwordResetService.deleteToken(
                request.token()
        );

        auditService.log(
                new SecurityAuditEvent(
                        user,
                        SecurityEventType.PASSWORD_RESET_COMPLETED,
                        true,
                        null,
                        null,
                        "Password reset completed",
                        meta,
                        null,
                        ""
                )
        );
    }
}