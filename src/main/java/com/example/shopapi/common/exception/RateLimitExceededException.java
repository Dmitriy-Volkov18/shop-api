package com.example.shopapi.common.exception;

import com.example.shopapi.auth.enums.RateLimitType;

public class RateLimitExceededException extends RuntimeException {

    private final RateLimitType type;

    public RateLimitExceededException(RateLimitType type) {
        super(type.name() + " rate limit exceeded");
        this.type = type;
    }

    public RateLimitType getType() {
        return type;
    }
}