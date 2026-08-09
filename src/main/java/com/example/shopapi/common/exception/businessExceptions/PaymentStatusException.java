package com.example.shopapi.common.exception.businessExceptions;

public class PaymentStatusException extends BusinessException {

    public PaymentStatusException(
            String message
    ) {
        super(message);
    }
}