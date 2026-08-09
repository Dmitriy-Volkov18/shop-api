package com.example.shopapi.common.exception.businessExceptions;

public class EmptyCartException extends BusinessException {

    public EmptyCartException() {
        super("Cart is empty");
    }
}