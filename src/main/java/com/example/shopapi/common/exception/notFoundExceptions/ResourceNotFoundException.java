package com.example.shopapi.common.exception.notFoundExceptions;

public class ResourceNotFoundException extends NotFoundException {

    public ResourceNotFoundException() {
        super("Resource not found");
    }

}