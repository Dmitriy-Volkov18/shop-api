package com.example.shopapi.auth.services;

import com.example.shopapi.auth.dto.DeviceInfo;
import com.example.shopapi.auth.dto.SecurityAuditEvent;
import com.example.shopapi.auth.dto.SessionMeta;
import com.example.shopapi.auth.entities.SecurityAuditLog;
import com.example.shopapi.auth.repositories.SecurityAuditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class SecurityAuditService {

    private final SecurityAuditRepository repository;

    public void log(SecurityAuditEvent event) {

        SecurityAuditLog log = new SecurityAuditLog();

        log.setUser(event.user());
        log.setEventType(event.eventType());
        log.setSuccess(event.success());
        log.setCreatedAt(Instant.now());

        log.setDeviceId(event.deviceId());
        log.setJti(event.jti());

        log.setDetails(event.details());

        // 🧠 risk score logging
        log.setRiskLevel(event.riskLevel());

        // ❗ failure reason logging
        log.setFailureReason(event.failureReason());

        SessionMeta meta = event.meta();

        if (meta != null) {

            log.setIpAddress(meta.ip());
            log.setCountry(meta.country());
            log.setUserAgent(meta.userAgent());

            DeviceInfo device = meta.deviceInfo();

            if (device != null) {

                log.setDeviceName(device.getDeviceName());
                log.setDeviceType(device.getDeviceType());

                log.setBrowser(device.getBrowser());
                log.setBrowserVersion(device.getBrowserVersion());

                log.setOperatingSystem(device.getOperatingSystem());
                log.setOperatingSystemVersion(device.getOperatingSystemVersion());
            }
        }

        repository.save(log);
    }
}