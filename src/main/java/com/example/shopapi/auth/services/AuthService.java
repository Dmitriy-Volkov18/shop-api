package com.example.shopapi.auth.services;

import com.example.shopapi.auth.dto.AuthResponse;
import com.example.shopapi.auth.entities.DeviceInfo;
import com.example.shopapi.auth.dto.LoginRequest;
import com.example.shopapi.auth.dto.RegisterRequest;
import com.example.shopapi.auth.dto.RiskResult;
import com.example.shopapi.auth.dto.SecurityAuditEvent;
import com.example.shopapi.auth.dto.SessionMeta;
import com.example.shopapi.auth.entities.RefreshToken;
import com.example.shopapi.user.entities.User;
import com.example.shopapi.auth.enums.RiskLevel;
import com.example.shopapi.auth.enums.SecurityDecision;
import com.example.shopapi.auth.enums.SecurityEventType;
import com.example.shopapi.user.enums.UserRole;
import com.example.shopapi.common.exception.runtimeExceptions.BadRequestException;
import com.example.shopapi.common.exception.conflictExceptions.ConflictException;
import com.example.shopapi.user.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;
    private final RefreshTokenService refreshTokenService;
    private final SecurityAuditService auditService;
    private final SessionLimitService sessionLimitService;
    private final UserAgentParser userAgentParser;
    private final RiskEngine riskEngine;
    private final EmailVerificationService emailVerificationService;
    private final MailService mailService;

    private AuthResponse createSession(
            User user,
            String deviceId,
            String familyId,
            SessionMeta meta,
            RiskLevel riskLevel,
            SecurityEventType eventType,
            String auditMessage
    ) {
        String accessToken = jwtService.generateAccessToken(user);

        String refreshToken =
                jwtService.generateRefreshToken(
                        user,
                        deviceId,
                        familyId
                );

        String jti = jwtService.extractJti(refreshToken);

        sessionLimitService.enforce(user, meta);

        DeviceInfo deviceInfo = userAgentParser.parse(meta.userAgent());

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

        auditService.log(
                new SecurityAuditEvent(
                        user,
                        eventType,
                        true,
                        deviceId,
                        jti,
                        auditMessage,
                        meta,
                        riskLevel,
                        ""
                )
        );

        return new AuthResponse(
                accessToken,
                refreshToken,
                deviceId
        );
    }

    private AuthResponse createSession(
            User user,
            SessionMeta meta,
            RiskLevel riskLevel,
            SecurityEventType eventType,
            String auditMessage
    ) {
        return createSession(
                user,
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                meta,
                riskLevel,
                eventType,
                auditMessage
        );
    }


    private User createUser(RegisterRequest request){
        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(UserRole.USER);
        user = userRepository.save(user);

        return user;
    }


    public AuthResponse register(RegisterRequest request, SessionMeta meta, RiskLevel riskLevel) {
        if (userRepository.existsByUsername(request.username())) {
            throw new ConflictException("Username already exists");
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new ConflictException("Email already exists");
        }

        User user = createUser(request);

        String verificationToken = emailVerificationService.createToken(user);

        mailService.send(
                user.getEmail(),
                "Verify your email",
                """
                Welcome!
        
                Please verify your email:
        
                http://localhost:8080/auth/verify-email?token=%s
                """.formatted(verificationToken)
        );

        AuthResponse response = createSession(
                user,
                meta,
                riskLevel,
                SecurityEventType.REGISTER,
                "User registered"
        );

        return response;
    }

    public AuthResponse login(
            LoginRequest request,
            SessionMeta meta
    ) {
        try{
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.username(),
                            request.password()
                    )
            );
        }catch(AuthenticationException ex){
            userRepository.findByUsername(request.username())
                    .ifPresent(user ->
                            auditService.log(
                                    new SecurityAuditEvent(
                                            user,
                                            SecurityEventType.LOGIN_FAILED,
                                            false,
                                            null,
                                            null,
                                            "Invalid credentials",
                                            meta,
                                            RiskLevel.LOW,
                                            "BAD_CREDENTIALS"
                                    )
                            )
                    );



            throw new BadRequestException("Invalid username or password");
        }

        User user = userRepository.findByUsername(request.username())
                .orElseThrow();

        if (!user.isEmailVerified()) {

            auditService.log(
                    new SecurityAuditEvent(
                            user,
                            SecurityEventType.LOGIN_FAILED,
                            false,
                            null,
                            null,
                            "Login blocked: email not verified",
                            meta,
                            RiskLevel.LOW,
                            "EMAIL_NOT_VERIFIED"
                    )
            );

            throw new BadRequestException(
                    "Email verification required"
            );
        }

        List<RefreshToken> sessions =
                refreshTokenService.getSessionHistory(user.getId());

        RiskResult risk =
                riskEngine.evaluateLogin(
                        user,
                        sessions,
                        meta
                );

        if (risk.decision() == SecurityDecision.BLOCK) {

            auditService.log(
                    new SecurityAuditEvent(
                            user,
                            SecurityEventType.LOGIN_FAILED,
                            false,
                            null,
                            null,
                            "Suspicious login blocked",
                            meta,
                            risk.level(),
                            "BLOCKED"
                    )
            );

            throw new BadRequestException(
                    "Suspicious login detected"
            );
        }

        if (risk.decision() == SecurityDecision.STEP_UP_AUTH) {

            auditService.log(
                    new SecurityAuditEvent(
                            user,
                            SecurityEventType.LOGIN_FAILED,
                            false,
                            null,
                            null,
                            "Step-up authentication required",
                            meta,
                            risk.level(),
                            "STEP_UP_REQUIRED"
                    )
            );

            throw new BadRequestException(
                    "Step-up authentication required"
            );
        }

        LocalDateTime now = LocalDateTime.now();

        user.setLastLoginAt(now);
        user.setLastActivityAt(now);

        userRepository.save(user);

        return createSession(
                user,
                meta,
                risk.level(),
                SecurityEventType.LOGIN,
                "Successful login"
        );
    }


    public AuthResponse rotateRefreshSession(
            RefreshToken session,
            SessionMeta meta,
            RiskLevel risk
    ) {
        String deviceId = session.getDeviceIdentity().getDeviceId();
        String familyId = session.getFamilyId();

        User user = session.getUser();

        refreshTokenService.revoke(session);

        return createSession(
                user,
                deviceId,
                familyId,
                meta,
                risk,
                SecurityEventType.REFRESH,
                "Refresh token rotated"
        );
    }

}