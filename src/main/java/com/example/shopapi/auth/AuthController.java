package com.example.shopapi.auth;

import com.example.shopapi.auth.dto.*;
import com.example.shopapi.auth.enums.RateLimitType;
import com.example.shopapi.auth.enums.RiskLevel;
import com.example.shopapi.auth.services.*;
import com.example.shopapi.auth.security.SessionMetaProvider;
import com.example.shopapi.user.entities.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(
        name = "Authentication",
        description = "Регистрация, аутентификация и управление учётной записью"
)
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;
    private final SessionMapper sessionMapper;
    private final SessionMetaProvider sessionMetaProvider;
    private final AdaptiveRateLimitService adaptiveRateLimitService;
    private final RefreshSecurityService refreshSecurityService;
    private final UserSessionService userSessionService;
    private final EmailVerificationFacade emailVerificationFacade;
    private final PasswordManagementService passwordManagementService;
    private final CurrentUserService currentUserService;


    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account and returns access and refresh tokens."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "User successfully registered"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid registration data"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Username or email already exists"
            ),
            @ApiResponse(
                    responseCode = "429",
                    description = "Registration rate limit exceeded"
            )
    })
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletRequest httpRequest
    ) {
        SessionMeta meta = sessionMetaProvider.build(httpRequest);
        RiskLevel risk = RiskLevel.LOW;

        adaptiveRateLimitService.check(
                meta.ip(),
                request.getUsername(),
                RateLimitType.REGISTER,
                risk
        );

        return authService.register(request, meta, risk);
    }


    @Operation(
            summary = "Login",
            description = "Authenticates a user and creates a new session."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Login successful"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid username/password or email is not verified"
            ),
            @ApiResponse(
                    responseCode = "429",
                    description = "Login rate limit exceeded"
            )
    })
    @PostMapping("/login")
    public AuthResponse login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest
    ) {
        SessionMeta meta = sessionMetaProvider.build(httpRequest);
        RiskLevel risk = RiskLevel.LOW;

        adaptiveRateLimitService.check(
                meta.ip(),
                request.getUsername(),
                RateLimitType.LOGIN,
                risk
        );

        return authService.login(request, meta);
    }


    @Operation(
            summary = "Refresh access token",
            description = "Validates a refresh token, consumes the current session token and creates a new refresh/access token pair."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Tokens successfully refreshed"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid, expired, revoked or suspicious refresh token"
            ),
            @ApiResponse(
                    responseCode = "429",
                    description = "Refresh rate limit exceeded"
            )
    })
    @PostMapping("/refresh")
    public AuthResponse refresh(
            @Valid @RequestBody RefreshRequest request,
            HttpServletRequest httpRequest
    ) {
        SessionMeta meta = sessionMetaProvider.build(httpRequest);

        return refreshSecurityService.refresh(
                request.getRefreshToken(),
                meta
        );
    }


    @Operation(
            summary = "Logout current session",
            description = "Revokes the current refresh session and blacklists the associated access token.",
            security = @SecurityRequirement(name = "bearer-key")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Current session successfully logged out"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid refresh token"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Refresh token does not belong to the current user"
            )
    })
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(
            @Valid @RequestBody LogoutRequest request,
            HttpServletRequest httpRequest,
            @RequestHeader("Authorization") String authorization
    ) {
        SessionMeta meta = sessionMetaProvider.build(httpRequest);

        userSessionService.logoutCurrentSession(
                request.getRefreshToken(),
                authorization,
                meta
        );
    }


    @Operation(
            summary = "Logout a specific session",
            description = "Revokes a single user session identified by its refresh token JTI.",
            security = @SecurityRequirement(name = "bearer-key")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Session successfully logged out"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Session not found or invalid JTI"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            )
    })
    @DeleteMapping("/sessions/{jti}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logoutSession(
            @PathVariable String jti,
            HttpServletRequest httpRequest
    ) {
        SessionMeta meta = sessionMetaProvider.build(httpRequest);

        userSessionService.logoutSession(
                jti,
                meta
        );
    }


    @Operation(
            summary = "Logout from all devices",
            description = "Revokes all refresh sessions and increments the user's token version, invalidating existing access tokens.",
            security = @SecurityRequirement(name = "bearer-key")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "All sessions successfully logged out"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            )
    })
    @DeleteMapping("/sessions")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logoutAllDevices(
            HttpServletRequest httpRequest
    ) {
        SessionMeta meta = sessionMetaProvider.build(httpRequest);

        userSessionService.logoutAllDevices(meta);
    }


    @Operation(
            summary = "Logout from all other devices",
            description = "Revokes all user sessions except the currently authenticated session.",
            security = @SecurityRequirement(name = "bearer-key")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Other sessions successfully logged out"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            )
    })
    @DeleteMapping("/sessions/others")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logoutOtherDevices(
            @RequestHeader("Authorization") String authHeader,
            HttpServletRequest httpRequest
    ) {
        String accessToken =
                jwtService.extractAccessToken(authHeader);

        String currentJti =
                jwtService.extractJti(accessToken);

        SessionMeta meta =
                sessionMetaProvider.build(httpRequest);

        userSessionService.logoutAllExcept(
                currentJti,
                meta
        );
    }


    @Operation(
            summary = "Change password",
            description = "Changes the password of the currently authenticated user.",
            security = @SecurityRequirement(name = "bearer-key")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Password successfully changed"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid password data"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            )
    })
    @PostMapping("/change-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            HttpServletRequest httpRequest
    ) {
        SessionMeta meta = sessionMetaProvider.build(httpRequest);

        passwordManagementService.changePassword(
                request,
                meta
        );
    }


    @Operation(
            summary = "Request password reset",
            description = "Sends a password reset email for the specified account."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Password reset request processed"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request"
            )
    })
    @PostMapping("/forgot-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request,
            HttpServletRequest httpRequest
    ) {
        SessionMeta meta = sessionMetaProvider.build(httpRequest);

        passwordManagementService.forgotPassword(
                request,
                meta
        );
    }


    @Operation(
            summary = "Reset password",
            description = "Resets the user's password using a valid reset token."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Password successfully reset"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid or expired reset token"
            )
    })
    @PostMapping("/reset-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resetPassword(
            @Valid @RequestBody ResetPasswordRequest request,
            HttpServletRequest httpRequest
    ) {
        SessionMeta meta = sessionMetaProvider.build(httpRequest);

        passwordManagementService.resetPassword(
                request,
                meta
        );
    }


    @Operation(
            summary = "Verify email",
            description = "Verifies a user's email address using the verification token."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Email successfully verified"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid or expired verification token"
            )
    })
    @GetMapping("/verify-email")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void verifyEmail(
            @RequestParam String token
    ) {
        emailVerificationFacade.verifyEmail(token);
    }


    @Operation(
            summary = "Resend email verification",
            description = "Sends a new email verification message."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Verification email request processed"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request"
            )
    })
    @PostMapping("/resend-verification")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resendVerification(
            @Valid @RequestBody ResendVerificationRequest request,
            HttpServletRequest httpRequest
    ) {
        SessionMeta meta = sessionMetaProvider.build(httpRequest);

        emailVerificationFacade.resendVerificationEmail(
                request.getEmail(),
                meta
        );
    }


    @Operation(
            summary = "Get active sessions",
            description = "Returns all currently active sessions of the authenticated user.",
            security = @SecurityRequirement(name = "bearer-key")
    )
    @ApiResponse(
            responseCode = "200",
            description = "Active sessions returned successfully"
    )
    @GetMapping("/sessions")
    public List<SessionResponse> sessions(
            @RequestHeader("Authorization") String authHeader
    ) {
        String accessToken =
                jwtService.extractAccessToken(authHeader);

        String currentJti =
                jwtService.extractJti(accessToken);

        User user =
                currentUserService.getCurrentUserEntity();

        return refreshTokenService
                .getActiveSessions(user.getId())
                .stream()
                .map(session ->
                        sessionMapper.toResponse(
                                session,
                                currentJti
                        )
                )
                .toList();
    }


    @Operation(
            summary = "Get session history",
            description = "Returns the session history of the authenticated user, including revoked sessions.",
            security = @SecurityRequirement(name = "bearer-key")
    )
    @ApiResponse(
            responseCode = "200",
            description = "Session history returned successfully"
    )
    @GetMapping("/sessions/history")
    public List<SessionResponse> sessionHistory(
            @RequestHeader("Authorization") String authHeader
    ) {
        String accessToken =
                jwtService.extractAccessToken(authHeader);

        String currentJti =
                jwtService.extractJti(accessToken);

        User user =
                currentUserService.getCurrentUserEntity();

        return refreshTokenService
                .getSessionHistory(user.getId())
                .stream()
                .map(session ->
                        sessionMapper.toResponse(
                                session,
                                currentJti
                        )
                )
                .toList();
    }


    @Operation(
            summary = "Trust a session",
            description = "Marks a specific session as trusted.",
            security = @SecurityRequirement(name = "bearer-key")
    )
    @PostMapping("/sessions/{jti}/trust")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void trustSession(
            @PathVariable String jti,
            HttpServletRequest request
    ) {
        User user =
                currentUserService.getCurrentUserEntity();

        SessionMeta meta =
                sessionMetaProvider.build(request);

        userSessionService.trustSession(
                user.getId(),
                jti,
                meta
        );
    }


    @Operation(
            summary = "Untrust a session",
            description = "Removes the trusted status from a specific session.",
            security = @SecurityRequirement(name = "bearer-key")
    )
    @PostMapping("/sessions/{jti}/untrust")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void untrustSession(
            @PathVariable String jti,
            HttpServletRequest request
    ) {
        User user =
                currentUserService.getCurrentUserEntity();

        SessionMeta meta =
                sessionMetaProvider.build(request);

        userSessionService.untrustSession(
                user.getId(),
                jti,
                meta
        );
    }


    @Operation(
            summary = "Update session nickname",
            description = "Updates the nickname of a specific session.",
            security = @SecurityRequirement(name = "bearer-key")
    )
    @PatchMapping("/sessions/{jti}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateSessionNickname(
            @PathVariable String jti,
            @Valid @RequestBody UpdateSessionRequest request,
            HttpServletRequest httpRequest
    ) {
        User user =
                currentUserService.getCurrentUserEntity();

        SessionMeta meta =
                sessionMetaProvider.build(httpRequest);

        userSessionService.updateSessionNickname(
                user.getId(),
                jti,
                request.getNickname(),
                meta
        );
    }
}