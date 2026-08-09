package com.example.shopapi.auth.services;

import com.example.shopapi.common.infrastructure.redis.RedisTokenService;
import com.example.shopapi.user.entities.User;
import com.example.shopapi.common.exception.BadRequestException;
import com.example.shopapi.auth.security.TokenHashUtil;
import com.example.shopapi.user.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private static final Duration TOKEN_LIFETIME = Duration.ofMinutes(15);

    private final RedisTokenService redisTokenService;
    private final UserRepository userRepository;

    @Transactional
    public String createToken(User user) {
        String rawToken = UUID.randomUUID().toString();
        String hash = TokenHashUtil.hash(rawToken);

        redisTokenService.savePasswordResetToken(
                hash,
                user.getId(),
                TOKEN_LIFETIME
        );

        return rawToken;
    }

    @Transactional(readOnly = true)
    public User validateToken(
            String rawToken
    ) {
        String hash = TokenHashUtil.hash(rawToken);
        Long userId = redisTokenService.getPasswordResetUserId(hash);

        if (userId == null) {
            throw new BadRequestException("Invalid or expired reset token");
        }

        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new BadRequestException("User not found")
                );
    }

    public void deleteToken(
            String rawToken
    ) {
        String hash = TokenHashUtil.hash(rawToken);
        redisTokenService.deletePasswordResetToken(hash);
    }

}