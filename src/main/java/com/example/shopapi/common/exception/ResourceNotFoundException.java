package com.example.shopapi.common.exception;

public class ResourceNotFoundException extends NotFoundException {

    public ResourceNotFoundException() {
        super("Resource not found");
    }

}