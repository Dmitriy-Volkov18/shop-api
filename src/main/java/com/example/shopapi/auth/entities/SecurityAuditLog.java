package com.example.shopapi.auth.entities;

import com.example.shopapi.auth.enums.RiskLevel;
import com.example.shopapi.auth.enums.SecurityEventType;
import com.example.shopapi.user.entities.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "security_audit_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SecurityAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SecurityEventType eventType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(length = 45)
    private String ipAddress;

    @Column(length = 100)
    private String country;

    @Column(length = 100)
    private String deviceName;

    @Column(length = 50)
    private String deviceType;

    @Column(length = 100)
    private String browser;

    @Column(length = 30)
    private String browserVersion;

    @Column(length = 100)
    private String operatingSystem;

    @Column(length = 30)
    private String operatingSystemVersion;

    @Column(length = 500)
    private String userAgent;

    @Column(length = 100)
    private String deviceId;

    @Column(length = 36)
    private String jti;

    @Column(nullable = false)
    private boolean success;

    @Column(length = 500)
    private String details;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private RiskLevel riskLevel;

    @Column(length = 500)
    private String failureReason;
}