package com.example.shopapi.common.exception.notFoundExceptions;

public class CategoryNotFoundException extends NotFoundException {

    public CategoryNotFoundException(Long id) {
        super("Category not found with id: " + id);
    }
}