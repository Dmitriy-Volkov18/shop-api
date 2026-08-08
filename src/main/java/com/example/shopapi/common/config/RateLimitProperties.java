package com.example.shopapi.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Getter
@Setter
@ConfigurationProperties(prefix = "security.rate-limit")
public class RateLimitProperties {

    private Limit login;
    private Limit register;
    private Limit refresh;
    private Limit emailVerification;
    private Limit passwordReset;

    @Getter
    @Setter
    public static class Limit {
        private long limit;
        private Duration window;
    }
}