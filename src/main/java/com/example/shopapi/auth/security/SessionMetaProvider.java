package com.example.shopapi.auth.security;

import com.example.shopapi.auth.dto.DeviceInfo;
import com.example.shopapi.auth.dto.SessionMeta;
import com.example.shopapi.auth.services.GeoService;
import com.example.shopapi.auth.services.UserAgentParser;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
    public class SessionMetaProvider {

    private final GeoService geoService;
    private final UserAgentParser userAgentParser;

    public SessionMetaProvider(
            GeoService geoService,
            UserAgentParser userAgentParser
    ) {
        this.geoService = geoService;
        this.userAgentParser = userAgentParser;
    }

    public SessionMeta build(HttpServletRequest request) {

        String userAgent = request.getHeader("User-Agent");

        String ip = extractIp(request);

        String country = geoService.resolveCountry(ip);

        DeviceInfo deviceInfo =
                userAgentParser.parse(userAgent);

        return new SessionMeta(
                ip,
                country,
                deviceInfo,
                userAgent
        );
    }

    private String extractIp(HttpServletRequest request) {

//        String ip = request.getHeader("X-Forwarded-For");
//
//        if (hasText(ip)) {
//            return ip.split(",")[0].trim();
//        }
//
//        ip = request.getHeader("X-Real-IP");
//
//        if (hasText(ip)) {
//            return ip;
//        }

        return request.getRemoteAddr();
    }

    private boolean hasText(String value) {
        return value != null
                && !value.isBlank()
                && !"unknown".equalsIgnoreCase(value);
    }
}