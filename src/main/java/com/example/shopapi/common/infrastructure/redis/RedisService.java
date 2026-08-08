package com.example.shopapi.common.infrastructure.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public void set(
            String key,
            Object value,
            Duration ttl
    ) {
        redisTemplate.opsForValue()
                .set(
                        key,
                        value,
                        ttl
                );
    }

    public Object get(
            String key
    ) {
        return redisTemplate.opsForValue()
                .get(key);
    }

    public <T> T get(
            String key,
            Class<T> type
    ) {
        Object value = get(key);

        if (value == null) {
            return null;
        }

        return type.cast(value);
    }

    public <T> T get(
            String key,
            TypeReference<T> type
    ) {

        Object value =
                redisTemplate.opsForValue()
                        .get(key);


        if (value == null) {
            return null;
        }


        return objectMapper.convertValue(
                value,
                type
        );
    }

    public void delete(
            String key
    ) {
        redisTemplate.delete(key);
    }

    public boolean exists(
            String key
    ) {
        Boolean result =
                redisTemplate.hasKey(key);

        return Boolean.TRUE.equals(result);
    }

    public Long increment(
            String key
    ) {
        return redisTemplate
                .opsForValue()
                .increment(key);
    }

    public void expire(
            String key,
            Duration ttl
    ) {
        redisTemplate.expire(
                key,
                ttl
        );
    }

    public Long ttl(
            String key
    ) {
        return redisTemplate.getExpire(
                key
        );
    }

    public void leftPush(
            String key,
            Object value
    ) {

        redisTemplate
                .opsForList()
                .leftPush(
                        key,
                        value
                );
    }


    public List<Object> range(
            String key,
            long start,
            long end
    ) {

        return redisTemplate
                .opsForList()
                .range(
                        key,
                        start,
                        end
                );
    }


    public void trim(
            String key,
            long start,
            long end
    ) {

        redisTemplate
                .opsForList()
                .trim(
                        key,
                        start,
                        end
                );
    }

    public void zAdd(
            String key,
            Object value,
            double score
    ) {

        redisTemplate
                .opsForZSet()
                .add(
                        key,
                        value,
                        score
                );
    }


    public Set<Object> zReverseRange(
            String key,
            long start,
            long end
    ) {

        return redisTemplate
                .opsForZSet()
                .reverseRange(
                        key,
                        start,
                        end
                );
    }


    public void zRemove(
            String key,
            Object value
    ) {

        redisTemplate
                .opsForZSet()
                .remove(
                        key,
                        value
                );
    }

    public Set<Object> zRange(
            String key,
            long start,
            long end
    ) {

        return redisTemplate
                .opsForZSet()
                .range(
                        key,
                        start,
                        end
                );
    }

    public Long zCard(
            String key
    ) {

        return redisTemplate
                .opsForZSet()
                .zCard(key);
    }

    public Double zIncrementScore(
            String key,
            Object member,
            double delta
    ) {

        return redisTemplate
                .opsForZSet()
                .incrementScore(
                        key,
                        member,
                        delta
                );
    }

    public void rename(
            String oldKey,
            String newKey
    ) {

        redisTemplate.rename(
                oldKey,
                newKey
        );
    }

    public void deleteByPattern(
            String pattern
    ) {

        Set<String> keys =
                redisTemplate.keys(pattern);

        if(keys != null && !keys.isEmpty()) {

            redisTemplate.delete(keys);
        }
    }

}