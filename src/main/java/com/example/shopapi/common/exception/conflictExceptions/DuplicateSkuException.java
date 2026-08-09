package com.example.shopapi.common.exception.conflictExceptions;

public class DuplicateSkuException extends ConflictException {

    public DuplicateSkuException(String sku) {
        super("SKU already exists: " + sku);
    }
}