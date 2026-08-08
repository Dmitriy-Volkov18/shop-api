package com.example.shopapi.order.services;

import com.example.shopapi.order.entities.CustomerOrder;
import com.example.shopapi.order.enums.OrderCancellationReason;
import com.example.shopapi.shipment.Shipment;
import com.example.shopapi.shipment.ShipmentStatus;
import com.example.shopapi.common.exception.OrderStatusException;
import com.example.shopapi.inventory.InventoryReservationService;
import com.example.shopapi.inventory.InventoryService;
import com.example.shopapi.payment.PaymentService;
import com.example.shopapi.shipment.ShipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderCancellationService {

    private final PaymentService paymentService;
    private final ShipmentService shipmentService;
    private final InventoryReservationService reservationService;
    private final InventoryService inventoryService;

    public void cancel(CustomerOrder order, OrderCancellationReason cancellationReason) {
        switch (order.getStatus()) {
            case PENDING -> cancelPending(order);
            case PAID -> cancelPaid(order);
            default -> throw new OrderStatusException(
                    "Order cannot be cancelled in status " + order.getStatus()
            );
        }

        order.cancel(cancellationReason);
    }

    private void cancelPending(CustomerOrder order) {
        paymentService.cancelIfPending(order.getPayment());
        reservationService.releaseByOrder(order);
        cancelShipment(order);
    }

    private void cancelPaid(CustomerOrder order) {
        paymentService.refundIfPaid(order);
        inventoryService.restoreFromOrder(order);
        cancelShipment(order);
    }

    private void cancelShipment(CustomerOrder order) {
        Shipment shipment = order.getShipment();

        if (shipment == null) {
            return;
        }

        if (shipment.getStatus() == ShipmentStatus.PENDING || shipment.getStatus() == ShipmentStatus.PROCESSING) {
            shipmentService.cancel(shipment);
        }
    }


}