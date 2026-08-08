package com.example.shopapi.inventory;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InventoryReservationScheduler {
    private final InventoryReservationService reservationService;

    @Scheduled(
            fixedDelayString = "${shop.reservation.cleanup-interval}"
    )
    public void releaseExpiredReservations(){
        reservationService.releaseExpired();
    }
}