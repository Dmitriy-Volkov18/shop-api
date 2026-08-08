package com.example.shopapi.common.config;

import com.example.shopapi.auth.filter.JwtAuthFilter;
import com.example.shopapi.user.UserActivityFilter;
import com.example.shopapi.auth.services.CustomUserDetailsService;
import com.example.shopapi.auth.security.JwtAccessDeniedHandler;
import com.example.shopapi.auth.security.JwtAuthenticationEntryPoint;
import com.example.shopapi.auth.filter.SessionMetadataFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final SessionMetadataFilter sessionMetadataFilter;
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;
    private final JwtAccessDeniedHandler accessDeniedHandler;
    private final UserActivityFilter userActivityFilter;

    public SecurityConfig(
            JwtAuthFilter jwtAuthFilter,
            JwtAuthenticationEntryPoint authenticationEntryPoint,
            JwtAccessDeniedHandler accessDeniedHandler,
            SessionMetadataFilter sessionMetadataFilter,
            UserActivityFilter userActivityFilter
    ) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
        this.sessionMetadataFilter = sessionMetadataFilter;
        this.userActivityFilter = userActivityFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        return http
                // 1. CSRF выключаем (REST API + JWT)
                .csrf(csrf -> csrf.disable())// JWT Bearer auth, CSRF not required.
                // Re-enable if authentication moves to cookies.

                // 2. Stateless сессии (JWT вместо session)
                .sessionManagement(sm -> sm
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 3. Обработка ошибок Security (401 / 403 JSON)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )

                // 4. Авторизация запросов
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
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
                        ).permitAll()
                        .anyRequest().authenticated()
                )

                // 5. JWT фильтр ДО UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(
                        userActivityFilter,
                        JwtAuthFilter.class
                )
                .addFilterBefore(
                        sessionMetadataFilter,
                        UsernamePasswordAuthenticationFilter.class
                )
                .headers(headers -> headers

                        // запрещает открывать сайт внутри iframe
                        .frameOptions(frame -> frame.deny())

                        // запрещает MIME sniffing
                        .contentTypeOptions(Customizer.withDefaults())

                        // HSTS
                        // ПОЛЕЗНО В PRODUCTION
//                        .httpStrictTransportSecurity(hsts ->
//                                hsts.requestMatcher(request ->
//                                        request.isSecure()
//                                )
//                        )

                        // CSP
                        // КОГДА БУДЕТ FRONTEND НУЖНО СКОРРЕКТИРОВАТЬ
                        .contentSecurityPolicy(csp -> csp
                                .policyDirectives(
                                        "default-src 'self'; " +
                                                "object-src 'none'; " +
                                                "frame-ancestors 'none'; " +
                                                "base-uri 'self';"
                                )
                        )

                        // Referrer Policy
                        .referrerPolicy(referrer -> referrer
                                .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER)
                        )

                        // Permissions Policy
                        .addHeaderWriter(
                                new StaticHeadersWriter(
                                        "Permissions-Policy",
                                        "camera=(), microphone=(), geolocation=()"
                                )
                        )
                )
                .build();
    }

    // AuthenticationManager (нужен для login)
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }

    // DaoAuthenticationProvider (работает с UserDetailsService + BCrypt)
    @Bean
    public AuthenticationProvider authenticationProvider(
            CustomUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder
    ) {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService);

        provider.setPasswordEncoder(passwordEncoder);

        return provider;
    }
}