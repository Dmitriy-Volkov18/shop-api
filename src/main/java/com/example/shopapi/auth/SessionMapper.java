package com.example.shopapi.auth;

import com.example.shopapi.auth.dto.SessionResponse;
import com.example.shopapi.auth.entities.RefreshToken;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.time.Instant;
import java.util.Objects;

@Mapper(componentModel = "spring")
public interface SessionMapper {

    @Mappings({
            @Mapping(target = "deviceId",
                    source = "deviceIdentity.deviceId"),

            @Mapping(target = "deviceName",
                    source = "deviceIdentity.deviceInfo.deviceName"),

            @Mapping(target = "deviceType",
                    source = "deviceIdentity.deviceInfo.deviceType"),

            @Mapping(target = "browser",
                    source = "deviceIdentity.deviceInfo.browser"),

            @Mapping(target = "browserVersion",
                    source = "deviceIdentity.deviceInfo.browserVersion"),

            @Mapping(target = "operatingSystem",
                    source = "deviceIdentity.deviceInfo.operatingSystem"),

            @Mapping(target = "operatingSystemVersion",
                    source = "deviceIdentity.deviceInfo.operatingSystemVersion"),

            @Mapping(target = "ipAddress",
                    source = "ipAddress"),

            @Mapping(target = "country",
                    source = "country"),

            @Mapping(target = "createdAt",
                    source = "createdAt"),

            @Mapping(target = "lastUsedAt",
                    source = "lastUsedAt"),

            @Mapping(target = "expiryDate",
                    source = "expiryDate"),

            @Mapping(
                    target = "nickname",
                    source = "nickname"
            ),

            @Mapping(
                    target = "trusted",
                    source = "trusted"
            ),

            @Mapping(target = "current", expression = "java(mapCurrent(token, currentJti))"),
            @Mapping(target = "active", expression = "java(mapActive(token))")
    })
    SessionResponse toResponse(
            RefreshToken token,
            @Context String currentJti
    );

    default boolean mapCurrent(
            RefreshToken token,
            @Context String currentJti
    ) {

        return Objects.equals(
                token.getJti(),
                currentJti
        );
    }

    default boolean mapActive(RefreshToken token) {
        return !token.isRevoked()
                && token.getExpiryDate().isAfter(Instant.now());
    }
}