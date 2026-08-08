package com.example.shopapi.order.services;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderExpirationScheduler {


    private final OrderExpirationService service;


    @Scheduled(
            fixedDelayString =
                    "${shop.reservation.cleanup-interval}"
    )
    public void cancelExpiredOrders(){


        service.cancelExpiredOrders();

    }
}