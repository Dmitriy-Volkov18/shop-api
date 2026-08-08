package com.example.shopapi.inventory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InventoryReservationRepository
        extends JpaRepository<InventoryReservation, Long> {


    List<InventoryReservation>
    findByOrderId(Long orderId);


    List<InventoryReservation>
    findByStatusAndExpiresAtBefore(
            InventoryReservationStatus status,
            LocalDateTime time
    );

}