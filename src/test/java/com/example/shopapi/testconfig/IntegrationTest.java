package com.example.shopapi.testconfig;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public abstract class IntegrationTest {

    // 1. Объявляем контейнер PostgreSQL (версию берем как в вашем docker-compose)
    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>(
                    DockerImageName.parse("postgres:17")
            )
                    .withDatabaseName("shop")
                    .withUsername("postgres")
                    .withPassword("shoppassword");

    // 2. Объявляем контейнер Redis (используем GenericContainer для Redis)
    @Container
    static GenericContainer<?> redis =
            new GenericContainer<>(
                    DockerImageName.parse("redis:8-alpine") // alpine версия весит меньше и качается быстрее
            )
                    .withExposedPorts(6379);



    // 4. Переопределяем свойства dynamic-конфигурации, чтобы Spring знал куда подключаться
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // Подставляем динамический URL и порты поднятых тест-контейнеров
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
    }
}
