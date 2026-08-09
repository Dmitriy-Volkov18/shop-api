package com.example.shopapi.auth.services;

import com.example.shopapi.auth.entities.DeviceInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua_parser.Client;
import ua_parser.Parser;

@Component
@RequiredArgsConstructor
public class UserAgentParser {

    private final Parser parser = new Parser();
    private final DeviceTypeResolver deviceTypeResolver;

    public DeviceInfo parse(String userAgent) {
        if (userAgent == null || userAgent.isBlank()) {
            return new DeviceInfo(
                    "Unknown",
                    "",
                    "Unknown",
                    "",
                    "Unknown",
                    "Unknown"
            );
        }

        Client client = parser.parse(userAgent);
        String browser = client.userAgent.family;
        String browserVersion = firstNonNull(client.userAgent.major);
        String os = client.os.family;
        String osVersion = firstNonNull(client.os.major);
        String device = client.device.family;

        if ("Other".equals(device)) {
            device = "Desktop";
        }

        String deviceType = deviceTypeResolver.resolve(os, device);

        return new DeviceInfo(
                browser,
                browserVersion,
                os,
                osVersion,
                device,
                deviceType
        );
    }

    private String firstNonNull(String value) {
        return value == null ? "" : value;
    }

}