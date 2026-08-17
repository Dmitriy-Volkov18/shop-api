package com.example.shopapi.inventory;

import com.example.shopapi.common.config.ShopProperties;
import com.example.shopapi.order.entities.CustomerOrder;
import com.example.shopapi.productVariant.entities.ProductVariant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class InventoryReservationService {
    private final InventoryReservationRepository repository;
    private final ShopProperties properties;

    public InventoryReservation create(
            CustomerOrder order,
            ProductVariant variant,
            int quantity
    ){
        variant.reserve(quantity);

        InventoryReservation reservation = new InventoryReservation();
        reservation.setOrder(order);
        reservation.setVariant(variant);
        reservation.setQuantity(quantity);
        reservation.setStatus(InventoryReservationStatus.ACTIVE);
        reservation.setExpiresAt(
                LocalDateTime.now()
                        .plus(properties.getReservation().getTimeout())
        );

        log.info("Reservation is created");

        return repository.save(reservation);
    }

    public void confirm(
            InventoryReservation reservation
    ) {
        if(!reservation.getStatus().isActive()){
            log.warn("Reservation status is not active");

            return;
        }

        reservation.confirm();
        reservation.getVariant()
                .confirmReservation(
                        reservation.getQuantity()
                );

        log.info("Reservation is confirmed");
    }

    public void confirmByOrder(
            CustomerOrder order
    ) {
        List<InventoryReservation> reservations =
                repository.findByOrderId(
                        order.getId()
                );

        for(InventoryReservation reservation : reservations) {
            confirm(reservation);
        }

        log.info("Reservation is confirmed by order");
    }

    public void releaseByOrder(
            CustomerOrder order
    ) {
        List<InventoryReservation> reservations =
                repository.findByOrderId(
                        order.getId()
                );

        for (InventoryReservation reservation : reservations) {
            if(!reservation.getStatus().isActive()) {
                continue;
            }

            reservation.release();
            reservation.getVariant()
                    .releaseReservation(
                            reservation.getQuantity()
                    );
        }

        log.info("Reservation is released by order");
    }

    public void releaseExpired() {
        List<InventoryReservation> reservations =
                repository.findByStatusAndExpiresAtBefore(
                        InventoryReservationStatus.ACTIVE,
                        LocalDateTime.now()
                );

        for (InventoryReservation reservation : reservations) {
            reservation.expire();
            reservation.getVariant()
                    .releaseReservation(
                            reservation.getQuantity()
                    );
        }

        log.info("Reservation release is expired");
    }
}