package com.example.shopapi.common.exception;

public class ConflictException extends ApiException {

    public ConflictException(String message) {
        super(message);
    }
}