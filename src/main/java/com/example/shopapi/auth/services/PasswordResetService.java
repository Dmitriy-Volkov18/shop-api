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

    private static final Duration TOKEN_LIFETIME =
            Duration.ofMinutes(15);

    private final RedisTokenService redisTokenService;
    private final UserRepository userRepository;


    @Transactional
    public String createToken(User user) {

        String rawToken =
                UUID.randomUUID().toString();


        String hash =
                TokenHashUtil.hash(rawToken);


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

        String hash =
                TokenHashUtil.hash(rawToken);


        Long userId =
                redisTokenService
                        .getPasswordResetUserId(hash);


        if (userId == null) {
            throw new BadRequestException(
                    "Invalid or expired reset token"
            );
        }


        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new BadRequestException(
                                "User not found"
                        )
                );
    }

    public void deleteToken(
            String rawToken
    ) {

        String hash =
                TokenHashUtil.hash(rawToken);


        redisTokenService.deletePasswordResetToken(
                hash
        );
    }

    /*private static final Duration TOKEN_LIFETIME = Duration.ofMinutes(15);

    private final PasswordResetTokenRepository repository;

    @Transactional
    public String createToken(User user) {
        Instant now = Instant.now();

        // удаляем старые токены пользователя
        repository.deleteAllByUserId(user.getId());

        String rawToken = UUID.randomUUID().toString();

        PasswordResetToken token = new PasswordResetToken();
        token.setUser(user);
        // в БД только hash
        token.setTokenHash(TokenHashUtil.hash(rawToken));
        token.setCreatedAt(now);
        token.setExpiresAt(now.plus(TOKEN_LIFETIME));
        token.setUsed(false);

        repository.save(token);

        // возвращаем оригинальный токен,
        // он уйдёт в email
        return rawToken;
    }

    private void validate(
            PasswordResetToken token
    ) {
        if (token.isUsed()) {
            throw new BadRequestException(
                    "Reset token already used"
            );
        }

        if (token.getExpiresAt().isBefore(Instant.now())) {
            throw new BadRequestException(
                    "Reset token expired"
            );
        }
    }

    @Transactional(readOnly = true)
    public PasswordResetToken validateToken(
            String rawToken
    ) {
        String hash = TokenHashUtil.hash(rawToken);

        PasswordResetToken token =
                repository.findByTokenHash(hash)
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Invalid reset token"
                                ));

        validate(token);

        return token;
    }

    @Transactional
    public void deleteAllForUser(Long userId) {
        repository.deleteAllByUserId(userId);
    }

    @Transactional
    public void cleanupExpired() {
        Instant now = Instant.now();
        repository.deleteExpired(now);
    }*/
}