package com.example.shopapi.auth.security;

import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.List;

public final class SecurityEndpoints {

    private SecurityEndpoints() {
    }

    public static final List<String> PUBLIC_PATHS = List.of(
            "/auth/register",
            "/auth/login",
            "/auth/refresh",
            "/auth/forgot-password",
            "/auth/reset-password",
            "/auth/verify-email",
            "/auth/resend-verification",
            "/actuator/health",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-ui.html"
    );

    public static final RequestMatcher PUBLIC_ENDPOINTS =
            new OrRequestMatcher(
                    PathPatternRequestMatcher.withDefaults()
                            .matcher("/auth/register"),
                    PathPatternRequestMatcher.withDefaults()
                            .matcher("/auth/login"),
                    PathPatternRequestMatcher.withDefaults()
                            .matcher("/auth/refresh"),
                    PathPatternRequestMatcher.withDefaults()
                            .matcher("/auth/forgot-password"),
                    PathPatternRequestMatcher.withDefaults()
                            .matcher("/auth/reset-password"),
                    PathPatternRequestMatcher.withDefaults()
                            .matcher("/auth/verify-email"),
                    PathPatternRequestMatcher.withDefaults()
                            .matcher("/auth/resend-verification"),
                    PathPatternRequestMatcher.withDefaults()
                            .matcher("/actuator/health"),
                    PathPatternRequestMatcher.withDefaults()
                            .matcher("/swagger-ui/**"),
                    PathPatternRequestMatcher.withDefaults()
                            .matcher("/v3/api-docs/**"),
                    PathPatternRequestMatcher.withDefaults()
                            .matcher("/swagger-ui.html")
            );
}