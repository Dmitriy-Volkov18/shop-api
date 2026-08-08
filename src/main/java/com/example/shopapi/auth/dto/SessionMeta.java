package com.example.shopapi.auth.dto;

public record SessionMeta(
        String ip,
        String country,
        DeviceInfo deviceInfo,
        String userAgent
) {}