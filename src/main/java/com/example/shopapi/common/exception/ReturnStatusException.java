package com.example.shopapi.common.exception;

public class ReturnStatusException
        extends RuntimeException {


    public ReturnStatusException(
            String message
    ){

        super(message);
    }

}
