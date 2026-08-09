package com.example.shopapi.auth.dto;

import com.example.shopapi.auth.entities.DeviceInfo;

public record SessionMeta(
        String ip,
        String country,
        DeviceInfo deviceInfo,
        String userAgent
) {}