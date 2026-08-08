package com.example.shopapi.inventory;

import com.example.shopapi.common.config.ShopProperties;
import com.example.shopapi.order.entities.CustomerOrder;
import com.example.shopapi.productVariant.entities.ProductVariant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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

        return repository.save(reservation);
    }

    public void confirm(
            InventoryReservation reservation
    ) {
        if(!reservation.getStatus().isActive()){
            return;
        }

        reservation.confirm();
        reservation.getVariant()
                .confirmReservation(
                        reservation.getQuantity()
                );
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
    }
}