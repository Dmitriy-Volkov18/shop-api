package com.example.shopapi.common.exception;

public class ReviewNotFoundException
        extends NotFoundException {

    public ReviewNotFoundException(String message){
        super(message);
    }
    public ReviewNotFoundException(Long id) {
        super("Review not found with id: " + id);
    }

}