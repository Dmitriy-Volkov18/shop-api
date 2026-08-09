package com.example.shopapi.common.constants;

import java.time.Duration;

public class RedisCacheTImeConstants {

    private RedisCacheTImeConstants(){}

    public static final Duration PRODUCT_TTL = Duration.ofMinutes(30);
    public static final Duration PRODUCTS_LIST_TTL = Duration.ofMinutes(10);
    public static final Duration RECENTLY_VIEWED_TTL = Duration.ofDays(90);

}
