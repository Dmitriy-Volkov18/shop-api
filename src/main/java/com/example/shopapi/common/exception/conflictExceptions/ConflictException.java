package com.example.shopapi.common.exception.conflictExceptions;

import com.example.shopapi.common.exception.runtimeExceptions.ApiException;

public class ConflictException extends ApiException {

    public ConflictException(String message) {
        super(message);
    }
}