package com.example.shopapi.common.exception.notFoundExceptions;

public class ProductNotFoundException extends NotFoundException {

    public ProductNotFoundException(Long id) {
        super("Product not found with id: " + id);
    }

}