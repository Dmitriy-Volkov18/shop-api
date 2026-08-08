package com.example.shopapi.order.dto;

import com.example.shopapi.order.enums.CustomerOrderStatus;
import com.example.shopapi.order.enums.OrderCancellationReason;
import com.example.shopapi.payment.enums.PaymentStatus;
import com.example.shopapi.returnProducts.ReturnStatus;
import com.example.shopapi.shipment.ShipmentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CustomerOrderResponse(

        Long id,
        Long userId,

        LocalDateTime createdAt,
        LocalDateTime updatedAt,

        CustomerOrderStatus status,

        BigDecimal subtotal,
        BigDecimal couponDiscount,
        BigDecimal totalPrice,

        OrderAddressResponse shippingAddress,

        List<CustomerOrderItemResponse> items,

        PaymentStatus paymentStatus,
        ShipmentStatus shipmentStatus,
        ReturnStatus returnStatus,

        LocalDateTime paidAt,
        LocalDateTime shippedAt,
        LocalDateTime deliveredAt,
        LocalDateTime cancelledAt,

        OrderCancellationReason cancellationReason
) {}