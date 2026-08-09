package com.example.shopapi.auth.security;

import com.example.shopapi.auth.entities.DeviceInfo;
import com.example.shopapi.auth.dto.SessionMeta;
import com.example.shopapi.auth.services.GeoService;
import com.example.shopapi.auth.services.UserAgentParser;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SessionMetaProvider {

    private final GeoService geoService;
    private final UserAgentParser userAgentParser;

    public SessionMeta build(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        String ip = extractIp(request);
        String country = geoService.resolveCountry(ip);
        DeviceInfo deviceInfo = userAgentParser.parse(userAgent);

        return new SessionMeta(
                ip,
                country,
                deviceInfo,
                userAgent
        );
    }

    private String extractIp(HttpServletRequest request) {
        return request.getRemoteAddr();
    }

}