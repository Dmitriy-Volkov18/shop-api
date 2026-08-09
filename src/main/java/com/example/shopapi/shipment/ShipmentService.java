package com.example.shopapi.shipment;

import com.example.shopapi.common.exception.notFoundExceptions.ShipmentNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ShipmentService {
    private final ShipmentRepository shipmentRepository;

    public Shipment getById(
            Long id
    ){
        return shipmentRepository.findById(id)
                .orElseThrow(() ->
                        new ShipmentNotFoundException(id)
                );
    }

    public Shipment getByOrderId(
            Long orderId
    ){
        return shipmentRepository
                .findByOrderId(orderId)
                .orElseThrow(() ->
                        new ShipmentNotFoundException(orderId)
                );
    }

    public Shipment getForUser(
            Long shipmentId,
            Long userId
    ){
        return shipmentRepository
                .findByIdAndOrderUserId(
                        shipmentId,
                        userId
                )
                .orElseThrow(() ->
                        new ShipmentNotFoundException(
                                shipmentId
                        )
                );
    }

    public void process(
            Shipment shipment
    ){
        shipment.process();
    }

    public void ship(
            Shipment shipment,
            String carrier,
            String trackingNumber
    ){

        shipment.ship(
                carrier,
                trackingNumber
        );
    }

    public void deliver(
            Shipment shipment
    ){
        shipment.deliver();
    }

    public void cancel(
            Shipment shipment
    ){
        shipment.cancel();
    }
}