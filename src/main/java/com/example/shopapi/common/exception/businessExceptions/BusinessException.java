package com.example.shopapi.common.exception.businessExceptions;

import com.example.shopapi.common.exception.runtimeExceptions.ApiException;

public class BusinessException extends ApiException {

    public BusinessException(String message) {
        super(message);
    }
}