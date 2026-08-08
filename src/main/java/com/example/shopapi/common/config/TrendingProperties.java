package com.example.shopapi.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "shop.trending")
public record TrendingProperties(
        int periodDays,
        int purchaseWeight,
        int wishlistWeight,
        int viewWeight,
        String rebuildCron
) {
}