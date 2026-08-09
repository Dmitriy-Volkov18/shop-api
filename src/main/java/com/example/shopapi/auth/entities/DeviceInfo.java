package com.example.shopapi.auth.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeviceInfo {

    @Column(nullable = false)
    private String browser;

    @Column(nullable = false)
    private String browserVersion;

    @Column(nullable = false)
    private String operatingSystem;

    @Column(nullable = false)
    private String operatingSystemVersion;

    @Column(nullable = false)
    private String deviceName;

    @Column(nullable = false)
    private String deviceType;
}