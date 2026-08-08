package com.example.shopapi.common.exception;

public class CustomerOrderNotFoundException extends NotFoundException {

    public CustomerOrderNotFoundException(Long id) {
        super("Order not found with id: " + id);
    }
}