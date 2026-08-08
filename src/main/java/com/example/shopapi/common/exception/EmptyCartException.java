package com.example.shopapi.common.exception;

public class EmptyCartException extends BusinessException {

    public EmptyCartException() {
        super("Cart is empty");
    }
}