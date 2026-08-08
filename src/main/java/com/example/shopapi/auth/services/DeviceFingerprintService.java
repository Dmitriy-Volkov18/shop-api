package com.example.shopapi.auth.services;

import com.example.shopapi.auth.dto.DeviceInfo;
import com.example.shopapi.auth.security.TokenHashUtil;
import org.springframework.stereotype.Service;

@Service
public class DeviceFingerprintService {

    public String fingerprint(DeviceInfo device) {

        String raw =
                normalize(device.getBrowser()) + "|" +
                        normalize(device.getBrowserVersion()) + "|" +
                        normalize(device.getOperatingSystem()) + "|" +
                        normalize(device.getOperatingSystemVersion()) + "|" +
                        normalize(device.getDeviceType()) + "|" +
                        normalize(device.getDeviceName());

        return TokenHashUtil.hash(raw);
    }

    private String normalize(String value) {
        return value == null
                ? ""
                : value.trim().toLowerCase();
    }
}