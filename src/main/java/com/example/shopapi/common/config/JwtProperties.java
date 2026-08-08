package com.example.shopapi.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Getter
@Setter
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * Секретный ключ для подписи JWT.
     * Для HS256 рекомендуется не менее 32 байт.
     */
    private String secret;

    /**
     * Время жизни access token.
     */
    private Duration accessExpiration;

    /**
     * Время жизни refresh token.
     */
    private Duration refreshExpiration;

    private int maxSessions = 5;
}
