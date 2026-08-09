package com.example.shopapi.common.exception.runtimeExceptions;

public class BadRequestException extends ApiException {

    public BadRequestException(String message) {
        super(message);
    }
}