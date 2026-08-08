package com.example.shopapi.shipment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShipmentRepository
        extends JpaRepository<Shipment, Long> {

    Optional<Shipment> findByOrderId(Long orderId);
    Optional<Shipment> findByIdAndOrderUserId(
            Long shipmentId,
            Long userId
    );

}