package com.example.shopapi.common.exception.notFoundExceptions;

public class CartItemNotFoundException extends NotFoundException {

    public CartItemNotFoundException(Long id) {
        super("Cart item %d not found"
                .formatted(id));
    }
}