package com.example.shopapi.auth.filter;

import com.example.shopapi.auth.entities.DeviceIdentity;
import com.example.shopapi.auth.entities.DeviceInfo;
import com.example.shopapi.auth.repositories.RefreshTokenRepository;
import com.example.shopapi.auth.services.DeviceFingerprintService;
import com.example.shopapi.auth.services.GeoService;
import com.example.shopapi.auth.services.UserAgentParser;
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

        String jti = (String) request.getAttribute(
                JwtAuthFilter.JWT_JTI_ATTRIBUTE
        );

        if (jti == null) {
            filterChain.doFilter(request, response);
            return;
        }

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

            DeviceInfo newDeviceInfo = new DeviceInfo(
                    info.getBrowser(),
                    info.getBrowserVersion(),
                    info.getOperatingSystem(),
                    info.getOperatingSystemVersion(),
                    info.getDeviceName(),
                    info.getDeviceType()
            );

            DeviceIdentity identity = session.getDeviceIdentity();

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

            session.setLastUsedAt(now);
            session.setIpAddress(ip);
            session.setUserAgent(userAgent);
            session.setCountry(country);


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

        filterChain.doFilter(request, response);
    }
}