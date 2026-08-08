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
public class EmailVerificationService {

    private static final Duration TOKEN_LIFETIME = Duration.ofHours(24);

    private final RedisTokenService redisTokenService;
    private final UserRepository userRepository;

    @Transactional
    public String createToken(User user) {
        String rawToken = UUID.randomUUID().toString();
        String hash = TokenHashUtil.hash(rawToken);

        redisTokenService.saveEmailVerificationToken(
                hash,
                user.getId(),
                TOKEN_LIFETIME
        );

        redisTokenService.saveUserVerificationToken(
                user.getId(),
                hash,
                TOKEN_LIFETIME
        );

        return rawToken;
    }

    @Transactional
    public User verify(
            String rawToken
    ) {
        String hash = TokenHashUtil.hash(rawToken);
        Long userId = redisTokenService.getEmailVerificationUserId(hash);

        if (userId == null) {
            throw new BadRequestException(
                    "Invalid or expired verification token"
            );
        }

        User user =
                userRepository.findById(userId)
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "User not found"
                                )
                        );

        user.setEmailVerified(true);

        redisTokenService.deleteEmailVerificationToken(
                hash
        );

        redisTokenService.deleteUserVerificationToken(
                userId
        );

        return user;
    }

    @Transactional
    public String recreateToken(User user) {
        String oldHash =
                redisTokenService
                        .getUserVerificationTokenHash(
                                user.getId()
                        );

        if(oldHash != null){
            redisTokenService.deleteEmailVerificationToken(oldHash);
        }

        return createToken(user);
    }

   /* private static final Duration TOKEN_LIFETIME = Duration.ofHours(24);

    private final EmailVerificationTokenRepository repository;

    @Transactional
    public String createToken(User user) {
        Instant now = Instant.now();
        String rawToken = UUID.randomUUID().toString();

        EmailVerificationToken token = new EmailVerificationToken();
        token.setUser(user);
        token.setTokenHash(TokenHashUtil.hash(rawToken));
        token.setExpiryDate(now.plus(TOKEN_LIFETIME));
        token.setCreatedAt(now);

        repository.save(token);

        return rawToken;
    }

    private void validate(
            EmailVerificationToken token
    ) {
        if (token.isUsed()) {
            throw new BadRequestException(
                    "Token already used"
            );
        }

        if (token.getExpiryDate().isBefore(Instant.now())) {
            throw new BadRequestException(
                    "Token expired"
            );
        }
    }

    @Transactional
    public User verify(String rawToken) {
        String hash = TokenHashUtil.hash(rawToken);

        EmailVerificationToken token =
                repository.findByTokenHash(hash)
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Invalid verification token"
                                )
                        );

        validate(token);

        token.setUsed(true);

        User user = token.getUser();
        user.setEmailVerified(true);

        return user;
    }

    @Transactional
    public String recreateToken(User user) {
        repository.deleteByUserId(user.getId());

        return createToken(user);
    }

    @Transactional
    public void cleanupExpired() {
        Instant now = Instant.now();
        repository.deleteExpired(now);
    }*/
}