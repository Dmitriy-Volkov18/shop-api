package com.example.shopapi.auth.filter;

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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final TokenBlacklistService tokenBlacklistService;
    private final UserRepository userRepository;

    public JwtAuthFilter(
            JwtService jwtService,
            TokenBlacklistService tokenBlacklistService,
            UserRepository userRepository
    ) {
        this.jwtService = jwtService;
        this.tokenBlacklistService = tokenBlacklistService;
        this.userRepository = userRepository;
    }

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

            User user = userRepository.findById(userId).orElseThrow();

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
            }

        } catch (JwtException | IllegalArgumentException ex) {

        }
        catch(BadCredentialsException ex){

        }

        filterChain.doFilter(request, response);
    }
}