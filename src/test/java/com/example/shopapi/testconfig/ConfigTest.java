package com.example.shopapi.testconfig;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;


class ConfigTest extends IntegrationTest {


    @Autowired
    private Environment environment;


    @Test
    void printConfig() {

        System.out.println(
                environment.getProperty(
                        "spring.datasource.url"
                )
        );

        System.out.println(
                environment.getProperty(
                        "spring.datasource.username"
                )
        );

        System.out.println(
                environment.getProperty(
                        "spring.datasource.password"
                )
        );
    }
}