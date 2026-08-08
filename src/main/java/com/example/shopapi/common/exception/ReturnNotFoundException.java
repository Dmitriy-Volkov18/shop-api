package com.example.shopapi.common.exception;

public class ReturnNotFoundException
        extends NotFoundException {

    public ReturnNotFoundException(Long id) {
        super("Product not found with id: " + id);
    }

}