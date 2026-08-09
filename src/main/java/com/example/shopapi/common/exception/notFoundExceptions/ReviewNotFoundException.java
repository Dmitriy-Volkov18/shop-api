package com.example.shopapi.common.exception.notFoundExceptions;

public class ReviewNotFoundException extends NotFoundException {

    public ReviewNotFoundException(String message){
        super(message);
    }
    public ReviewNotFoundException(Long id) {
        super("Review not found with id: " + id);
    }

}