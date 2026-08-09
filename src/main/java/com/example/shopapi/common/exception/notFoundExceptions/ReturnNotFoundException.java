package com.example.shopapi.common.exception.notFoundExceptions;

public class ReturnNotFoundException extends NotFoundException {

    public ReturnNotFoundException(Long id) {
        super("Return not found with id: " + id);
    }

}