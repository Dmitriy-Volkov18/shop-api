package com.example.shopapi.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI shopApi() {

        return new OpenAPI()

                .info(
                        new Info()
                                .title("Shop API")
                                .description("""
                                        REST API интернет-магазина.

                                        API предоставляет возможности для:
                                        - регистрации и аутентификации пользователей;
                                        - управления товарами и категориями;
                                        - работы с вариантами товаров;
                                        - управления корзиной;
                                        - создания и обработки заказов;
                                        - отзывов и рейтингов;
                                        - управления пользовательскими сессиями.
                                        """)
                                .version("1.0.0")
                                .contact(
                                        new Contact()
                                                .name("Shop API")
                                )
                                .license(
                                        new License()
                                                .name("MIT")
                                )
                )

                .components(
                        new Components()
                                .addSecuritySchemes(
                                        "bearer-key",
                                        new SecurityScheme()
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                                .description(
                                                        "Введите JWT access token. " +
                                                                "Swagger автоматически добавит " +
                                                                "Bearer перед токеном."
                                                )
                                )
                )

                .externalDocs(
                        new ExternalDocumentation()
                                .description("Shop API Documentation")
                );
    }
}