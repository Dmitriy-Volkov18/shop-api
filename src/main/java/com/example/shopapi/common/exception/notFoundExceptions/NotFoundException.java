package com.example.shopapi.common.exception.notFoundExceptions;

import com.example.shopapi.common.exception.runtimeExceptions.ApiException;

public abstract class NotFoundException extends ApiException {

    protected NotFoundException(String message) {
        super(message);
    }
}