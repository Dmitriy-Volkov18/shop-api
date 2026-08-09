package com.example.shopapi.common.exception.runtimeExceptions;

public class PurchaseRequiredException extends BadRequestException {

    public PurchaseRequiredException(Long productId) {
        super("You can review only purchased products. Product id: " + productId);
    }
}