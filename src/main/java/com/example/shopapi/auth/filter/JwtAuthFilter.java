package com.example.shopapi.auth.filter;

import com.example.shopapi.auth.security.SecurityEndpoints;
import com.example.shopapi.user.CustomUserPrincipal;
import com.example.shopapi.user.entities.User;
import com.example.shopapi.user.repositories.UserRepository;
import com.example.shopapi.auth.services.JwtService;
import com.example.shopapi.auth.services.TokenBlacklistService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    public static final String JWT_JTI_ATTRIBUTE = JwtAuthFilter.class.getName() + ".jti";

    private final JwtService jwtService;
    private final TokenBlacklistService tokenBlacklistService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        if (SecurityEndpoints.PUBLIC_ENDPOINTS.matches(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = authHeader.substring(7);
            String jti = jwtService.extractJti(token);

            if (tokenBlacklistService.isRevoked(jti)) {
                throw new BadCredentialsException("Session revoked");
            }

            String username = jwtService.extractUsername(token);
            Long userId = jwtService.extractUserId(token);
            Long tokenVersion = jwtService.extractTokenVersion(token);

            User user = userRepository.findById(userId)
                    .orElseThrow(() ->
                            new BadCredentialsException("User not found")
                    );


            if (!Objects.equals(user.getTokenVersion(), tokenVersion)) {
                throw new BadCredentialsException("Token version invalid (session revoked)");
            }

            if (username != null &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {

                CustomUserPrincipal principal = new CustomUserPrincipal(user);

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                principal,
                                null,
                                principal.getAuthorities()
                        );

                SecurityContextHolder.getContext().setAuthentication(auth);

                request.setAttribute(
                        JWT_JTI_ATTRIBUTE,
                        jti
                );
            }

        } catch (JwtException | IllegalArgumentException ex) {
            SecurityContextHolder.clearContext();

            log.debug(
                    "JWT validation failed for {} {}",
                    request.getMethod(),
                    request.getRequestURI()
            );
        }
        catch(BadCredentialsException ex){
            SecurityContextHolder.clearContext();

            log.debug(
                    "JWT authentication failed for {} {}: {}",
                    request.getMethod(),
                    request.getRequestURI(),
                    ex.getMessage()
            );
        }

        filterChain.doFilter(request, response);
    }
}