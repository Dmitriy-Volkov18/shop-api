package com.example.shopapi.common.infrastructure.redis;

import com.example.shopapi.auth.enums.RateLimitType;
import com.example.shopapi.auth.enums.RiskLevel;
import com.example.shopapi.common.config.RedisProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisKeyBuilder {

    private final RedisProperties properties;

    public String rateLimit(
            String ip,
            String userKey,
            RateLimitType type,
            RiskLevel risk
    ) {
        return String.format(
                "%s:rate-limit:%s:%s:%s:%s",
                properties.getPrefix(),
                type,
                risk,
                ip,
                userKey
        );
    }

    public String product(
            Long productId
    ) {

        return String.format(
                "%s:cache:product:%d",
                properties.getPrefix(),
                productId
        );
    }

    public String category(Long categoryId){
        return String.format("%s:cache:category:%d", properties.getPrefix(), categoryId);
    }

    public String categoryTree(){
        return String.format("%s:cache:category-tree", properties.getPrefix());
    }

    public String recentlyViewed(Long userId){
        return String.format("%s:recently:viewed:%d", properties.getPrefix(), userId);
    }

    public String userSessions(
            Long userId
    ) {

        return String.format(
                "%s:user:sessions:%d",
                properties.getPrefix(),
                userId
        );
    }


    public String trendingProducts() {

        return String.format(
                "%s:trending",
                properties.getPrefix()
        );
    }

    public String trendingProductsTemp() {

        return String.format(
                "%s:trending:tmp",
                properties.getPrefix()
        );
    }

    public String emailVerification(
            String tokenHash
    ) {

        return String.format(
                "%s:verify-email:%s",
                properties.getPrefix(),
                tokenHash
        );
    }


    public String passwordReset(
            String tokenHash
    ) {

        return String.format(
                "%s:password-reset:%s",
                properties.getPrefix(),
                tokenHash
        );
    }

    public String emailVerificationByUser(
            Long userId
    ) {

        return String.format(
                "%s:verify-email:user:%s",
                properties.getPrefix(),
                userId
        );
    }

    public String revokedJti(
            String jti
    ) {

        return String.format(
                "%s:blacklist:%s",
                properties.getPrefix(),
                jti
        );
    }

    public String products(
            String hash
    ) {

        return String.format(
                "%s:cache:products:%s",
                properties.getPrefix(),
                hash
        );
    }
}