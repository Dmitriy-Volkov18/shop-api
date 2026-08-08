package com.example.shopapi.common.exception;


public class ShipmentNotFoundException
        extends NotFoundException {

    public ShipmentNotFoundException(Long id) {
        super("Product not found with id: " + id);
    }

}