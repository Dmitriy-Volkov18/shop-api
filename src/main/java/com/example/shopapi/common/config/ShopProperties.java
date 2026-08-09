package com.example.shopapi.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "shop")
@Getter
@Setter
public class ShopProperties {

    private final Reservation reservation = new Reservation();
    private final Order order = new Order();

    @Getter
    @Setter
    public static class Reservation {
        private Duration timeout = Duration.ofMinutes(15);
        private Duration cleanupInterval = Duration.ofMinutes(1);
    }

    @Getter
    @Setter
    public static class Order {
        private Duration paymentTimeout = Duration.ofMinutes(15);
    }
}