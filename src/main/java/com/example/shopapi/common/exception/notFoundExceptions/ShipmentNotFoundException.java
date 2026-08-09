package com.example.shopapi.common.exception.notFoundExceptions;


public class ShipmentNotFoundException extends NotFoundException {

    public ShipmentNotFoundException(Long id) {
        super("Shipment not found with id: " + id);
    }

}