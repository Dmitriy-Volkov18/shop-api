package com.example.shopapi.common.exception;

public abstract class NotFoundException extends ApiException {

    protected NotFoundException(String message) {
        super(message);
    }
}