package com.example.shopapi.user;

import com.example.shopapi.user.repositories.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class UserActivityFilter extends OncePerRequestFilter {

    private static final Duration UPDATE_INTERVAL = Duration.ofMinutes(5);

    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getServletPath();

        if (path.startsWith("/auth")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")) {

            filterChain.doFilter(request, response);
            return;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null
                && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof CustomUserPrincipal principal) {

            LocalDateTime now = LocalDateTime.now();
            LocalDateTime lastActivity = principal.getLastActivityAt();

            if (lastActivity == null || lastActivity.isBefore(now.minus(UPDATE_INTERVAL))) {
                userRepository.updateLastActivity(
                        principal.getUserId(),
                        now
                );

                principal.setLastActivityAt(now);
            }
        }

        filterChain.doFilter(request, response);
    }
}