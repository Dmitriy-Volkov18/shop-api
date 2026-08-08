package com.example.shopapi.auth.services;

import org.springframework.stereotype.Component;

@Component
public class DeviceTypeResolver {

    public String resolve(String operatingSystem, String deviceName) {

        if (operatingSystem == null) {
            return "Unknown";
        }

        String os = operatingSystem.toLowerCase();
        String device = deviceName == null
                ? ""
                : deviceName.toLowerCase();

        // iPad лучше проверить первым
        if (device.contains("ipad")) {
            return "Tablet";
        }

        if (os.contains("android")) {

            if (device.contains("tablet")) {
                return "Tablet";
            }

            return "Mobile";
        }

        if (os.contains("ios")) {
            return "Mobile";
        }

        return "Desktop";
    }
}