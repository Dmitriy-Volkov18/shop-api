package com.example.shopapi.common.exception;

public class CategoryNotFoundException extends NotFoundException {

    public CategoryNotFoundException(Long id) {
        super("Category not found with id: " + id);
    }
}