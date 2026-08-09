package com.example.shopapi.common.exception.notFoundExceptions;

public class CartNotFoundException extends NotFoundException {

    public CartNotFoundException(Long userId) {
        super("Cart for user %d not found"
                .formatted(userId));
    }
}