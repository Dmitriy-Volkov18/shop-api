package com.example.shopapi.auth.services;

import com.example.shopapi.auth.dto.SecurityAuditEvent;
import com.example.shopapi.auth.dto.SessionMeta;
import com.example.shopapi.auth.enums.RateLimitType;
import com.example.shopapi.auth.enums.RiskLevel;
import com.example.shopapi.auth.enums.SecurityEventType;
import com.example.shopapi.user.entities.User;
import com.example.shopapi.user.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class EmailVerificationFacade {

    private final UserRepository userRepository;
    private final EmailVerificationService emailVerificationService;
    private final AdaptiveRateLimitService adaptiveRateLimitService;
    private final SecurityAuditService auditService;
    private final MailService mailService;

    @Transactional
    public void verifyEmail(
            String token
    ) {
        User user = emailVerificationService.verify(token);

        auditService.log(
                new SecurityAuditEvent(
                        user,
                        SecurityEventType.EMAIL_VERIFIED,
                        true,
                        null,
                        null,
                        "Email verified",
                        null,
                        null,
                        ""
                )
        );
    }

    @Transactional
    public void resendVerificationEmail(
            String email,
            SessionMeta meta
    ) {
        adaptiveRateLimitService.check(
                meta.ip(),
                email,
                RateLimitType.EMAIL_VERIFICATION,
                RiskLevel.LOW
        );

        User user = userRepository.findByEmail(email)
                        .orElse(null);

        if (user == null || user.isEmailVerified()) {
            return;
        }

        String token = emailVerificationService.recreateToken(user);

        mailService.send(
                user.getEmail(),
                "Verify your email",
                """
                Welcome!
        
                Please verify your email:
        
                http://localhost:8080/auth/verify-email?token=%s
                """.formatted(token)
        );
    }

}