package com.example.shopapi.common.exception.notFoundExceptions;

public class AddressNotFoundException extends NotFoundException {

    public AddressNotFoundException(Long id) {
        super("Address not found with id: " + id);
    }

}