package com.example.shopapi.shipment;

import com.example.shopapi.order.entities.CustomerOrder;
import org.springframework.stereotype.Component;

@Component
public class ShipmentFactory {

    public Shipment create(
            CustomerOrder order
    ){
        Shipment shipment = new Shipment();
        shipment.setStatus(ShipmentStatus.PENDING);
        shipment.setOrder(order);

        return shipment;
    }
}