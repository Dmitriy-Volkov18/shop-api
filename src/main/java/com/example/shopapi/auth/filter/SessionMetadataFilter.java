package com.example.shopapi.auth.filter;

import com.example.shopapi.auth.dto.DeviceIdentity;
import com.example.shopapi.auth.dto.DeviceInfo;
import com.example.shopapi.auth.repositories.RefreshTokenRepository;
import com.example.shopapi.auth.services.DeviceFingerprintService;
import com.example.shopapi.auth.services.GeoService;
import com.example.shopapi.auth.services.JwtService;
import com.example.shopapi.auth.services.UserAgentParser;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class SessionMetadataFilter extends OncePerRequestFilter {

    private static final Duration UPDATE_INTERVAL = Duration.ofMinutes(5);

    private final JwtService jwtService;
    private final RefreshTokenRepository repository;
    private final GeoService geoService;
    private final UserAgentParser userAgentParser;
    private final DeviceFingerprintService deviceFingerprintService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String auth = request.getHeader("Authorization");

        if (auth == null || !auth.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = auth.substring(7);

        try {
            String jti = jwtService.extractJti(token);

            repository.findByJti(jti).ifPresent(session -> {

                Instant now = Instant.now();
                Instant lastUsed = session.getLastUsedAt();

                boolean timeToUpdate =
                        lastUsed == null ||
                                lastUsed.isBefore(now.minus(UPDATE_INTERVAL));

                if (!timeToUpdate) {
                    return;
                }

                String ip = request.getRemoteAddr();
                String userAgent = request.getHeader("User-Agent");
                String country = geoService.resolveCountry(ip);

                DeviceInfo info = userAgentParser.parse(userAgent);

                // ---- BUILD NEW DEVICE INFO AS SINGLE OBJECT ----
                DeviceInfo newDeviceInfo = new DeviceInfo(
                        info.getBrowser(),
                        info.getBrowserVersion(),
                        info.getOperatingSystem(),
                        info.getOperatingSystemVersion(),
                        info.getDeviceName(),
                        info.getDeviceType()
                );

                // ---- CHANGE DETECTION ----
                DeviceInfo old = session.getDeviceIdentity().getDeviceInfo();

                boolean changed =
                        old == null ||
                                !Objects.equals(old.getBrowser(), newDeviceInfo.getBrowser()) ||
                                !Objects.equals(old.getBrowserVersion(), newDeviceInfo.getBrowserVersion()) ||
                                !Objects.equals(old.getOperatingSystem(), newDeviceInfo.getOperatingSystem()) ||
                                !Objects.equals(old.getOperatingSystemVersion(), newDeviceInfo.getOperatingSystemVersion()) ||
                                !Objects.equals(old.getDeviceName(), newDeviceInfo.getDeviceName()) ||
                                !Objects.equals(old.getDeviceType(), newDeviceInfo.getDeviceType()) ||
                                !Objects.equals(session.getIpAddress(), ip) ||
                                !Objects.equals(session.getUserAgent(), userAgent) ||
                                !Objects.equals(session.getCountry(), country);

                if (!changed) {
                    return;
                }

                // ---- UPDATE SESSION ----
                session.setLastUsedAt(now);
                session.setIpAddress(ip);
                session.setUserAgent(userAgent);
                session.setCountry(country);

                DeviceIdentity identity = session.getDeviceIdentity();

                if (identity == null) {
                    identity = new DeviceIdentity();
                    session.setDeviceIdentity(identity);
                }

                identity.setDeviceInfo(newDeviceInfo);

                identity.setFingerprint(
                        deviceFingerprintService.fingerprint(newDeviceInfo)
                );

                repository.save(session);
            });

        } catch (JwtException | IllegalArgumentException ex) {

        }

        filterChain.doFilter(request, response);
    }
}