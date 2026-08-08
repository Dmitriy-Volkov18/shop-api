package com.example.shopapi.order.services;

import com.example.shopapi.coupon.services.CouponUsageService;
import com.example.shopapi.inventory.InventoryReservationService;
import com.example.shopapi.order.entities.CustomerOrder;
import com.example.shopapi.order.entities.CustomerOrderItem;
import com.example.shopapi.payment.Payment;
import com.example.shopapi.shipment.Shipment;
import com.example.shopapi.payment.PaymentService;
import com.example.shopapi.product.enums.ProductEventType;
import com.example.shopapi.product.services.ProductEventService;
import com.example.shopapi.shipment.ShipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderWorkflowService {

    private final PaymentService paymentService;
    private final ShipmentService shipmentService;
    private final InventoryReservationService reservationService;
    private final ProductEventService productEventService;
    private final CouponUsageService couponUsageService;

    public void pay(CustomerOrder order) {
        Payment payment = order.getPayment();

        paymentService.success(payment);

        reservationService.confirmByOrder(order);

        order.pay();

        couponUsageService.recordIfPresent(
                order
        );
    }

    public void ship(CustomerOrder order, String carrier, String trackingNumber) {
        Shipment shipment = order.getShipment();

        shipmentService.ship(
                shipment,
                carrier,
                trackingNumber
        );

        order.ship();
    }

    public void deliver(CustomerOrder order) {
        Shipment shipment = order.getShipment();

        shipmentService.deliver(
                shipment
        );

        order.deliver();
        recordSales(order);
    }

    private void recordSales(
            CustomerOrder order
    ) {
        for (CustomerOrderItem item : order.getItems()) {
            item.getVariant().getProduct().increaseSales(item.getQuantity());

            productEventService.record(
                    item.getVariant().getProduct(),
                    ProductEventType.PURCHASE,
                    item.getQuantity()
            );
        }
    }
}