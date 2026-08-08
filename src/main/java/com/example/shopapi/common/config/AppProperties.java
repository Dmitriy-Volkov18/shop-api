package com.example.shopapi.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
@Getter
@Setter
public class AppProperties {

    /**
     * Например:
     * http://localhost:3000
     */
    private String frontendUrl;

    /**
     * Email, отображаемый как отправитель.
     */
    private String mailFrom;
}