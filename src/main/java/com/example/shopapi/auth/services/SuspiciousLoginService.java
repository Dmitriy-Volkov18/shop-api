package com.example.shopapi.auth.services;

import com.example.shopapi.auth.dto.SessionMeta;
import com.example.shopapi.auth.entities.RefreshToken;
import com.example.shopapi.user.entities.User;
import com.example.shopapi.auth.enums.RiskLevel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class SuspiciousLoginService {


    public SuspiciousLoginService() {
    }

    public int calculateRiskOnLogin(User user, List<RefreshToken> previousSessions, SessionMeta meta) {
        int risk = 0;

        // если нет истории — минимальный риск
        if (previousSessions == null || previousSessions.isEmpty()) {
            return 10;
        }

        // сравниваем с последней активной сессией
        RefreshToken last = previousSessions.getFirst();

        risk += calculateRiskInternal(last, meta);

        return risk;
    }

    public int calculateRisk(RefreshToken stored, SessionMeta meta) {
        return calculateRiskInternal(stored, meta);
    }

    private int calculateRiskInternal(RefreshToken stored, SessionMeta meta) {

        int risk = 0;

        // 🔴 COUNTRY (самый сильный сигнал)
        if (!Objects.equals(stored.getCountry(), meta.country())) {
            risk += 50;
        }

        // 🟠 IP
        if (!Objects.equals(stored.getIpAddress(), meta.ip())) {
            risk += 20;
        }

        // 🟠 DEVICE
        if (!Objects.equals(stored.getDeviceIdentity().getDeviceInfo().getDeviceName(), meta.deviceInfo().getDeviceName())) {
            risk += 25;
        }

        // 🟡 USER AGENT
        if (!Objects.equals(stored.getUserAgent(), meta.userAgent())) {
            risk += 10;
        }

        // 🔴 COMBINED ATTACK SIGNAL
        if (isHighlySuspicious(stored, meta)) {
            risk += 20;
        }

        return risk;
    }

    private boolean isHighlySuspicious(RefreshToken stored, SessionMeta current) {

        boolean countryMismatch =
                !Objects.equals(stored.getCountry(), current.country());

        boolean ipMismatch =
                !Objects.equals(stored.getIpAddress(), current.ip());

        return countryMismatch && ipMismatch;
    }

    public RiskLevel evaluate(int riskScore) {

        if (riskScore <= 30) {
            return RiskLevel.LOW;
        }

        if (riskScore <= 70) {
            return RiskLevel.MEDIUM;
        }

        return RiskLevel.HIGH;
    }
}