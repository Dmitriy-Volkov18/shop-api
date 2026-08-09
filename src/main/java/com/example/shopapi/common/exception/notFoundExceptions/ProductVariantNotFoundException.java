package com.example.shopapi.common.exception.notFoundExceptions;

public class ProductVariantNotFoundException extends NotFoundException {

    public ProductVariantNotFoundException(Long id) {
        super("Variant not found: " + id);
    }

    public ProductVariantNotFoundException(String message) {
        super(message);
    }
}