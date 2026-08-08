package com.example.shopapi.common.exception;

public class PaymentStatusException
        extends BusinessException {

    public PaymentStatusException(
            String message
    ) {
        super(message);
    }
}