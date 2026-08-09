package com.example.shopapi.auth.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeviceIdentity {

    @Column(nullable = false)
    private String deviceId;

    @Column(nullable = false, length = 64)
    private String fingerprint;

    @Embedded
    private DeviceInfo deviceInfo;
}