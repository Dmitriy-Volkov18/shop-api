package com.example.shopapi.auth.services;

import com.example.shopapi.common.config.RateLimitProperties;
import com.example.shopapi.auth.enums.RateLimitType;
import com.example.shopapi.auth.enums.RiskLevel;
import com.example.shopapi.common.exception.runtimeExceptions.RateLimitExceededException;
import com.example.shopapi.common.infrastructure.redis.RedisKeyBuilder;
import com.example.shopapi.common.infrastructure.redis.ratelimit.RedisRateLimitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdaptiveRateLimitService {

    private final RateLimitProperties properties;
    private final RedisRateLimitService redisRateLimitService;
    private final RedisKeyBuilder redisKeyBuilder;

    public void check(String ip,
                      String userKey,
                      RateLimitType type,
                      RiskLevel risk) {

        RateLimitProperties.Limit limit =
                getLimit(type);

        String key =
                redisKeyBuilder.rateLimit(
                        ip,
                        userKey,
                        type,
                        risk
                );

        boolean allowed =
                redisRateLimitService.tryConsume(
                        key,
                        calculateLimit(limit, risk),
                        limit.getWindow()
                );


        if (!allowed) {
            log.warn(
                    "Rate limit exceeded. key={}",
                    key
            );

            throw new RateLimitExceededException(type);
        }
    }

    private RateLimitProperties.Limit getLimit(
            RateLimitType type
    ){
        return switch(type){

            case LOGIN ->
                    properties.getLogin();

            case REGISTER ->
                    properties.getRegister();

            case REFRESH ->
                    properties.getRefresh();

            case EMAIL_VERIFICATION ->
                    properties.getEmailVerification();

            case PASSWORD_RESET ->
                    properties.getPasswordReset();
        };
    }

    private long calculateLimit(
            RateLimitProperties.Limit limit,
            RiskLevel risk
    ){
        return switch(risk){
            case LOW ->
                    limit.getLimit();
            case MEDIUM ->
                    limit.getLimit()/2;
            case HIGH ->
                    limit.getLimit()/5;
        };
    }

}