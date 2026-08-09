package com.example.shopapi.common.exception.runtimeExceptions;

public abstract class ApiException extends RuntimeException {

    protected ApiException(String message) {
        super(message);
    }
}