package com.example.shopapi.common.exception;

public class PaymentNotFoundException
        extends NotFoundException {

    public PaymentNotFoundException(
            Long orderId
    ) {

        super(
                "Payment for order "
                        + orderId
                        + " not found"
        );
    }
}