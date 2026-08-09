package com.example.shopapi.common.exception.conflictExceptions;

public class ProductSkuAlreadyExistsException extends ConflictException {

    public ProductSkuAlreadyExistsException(String sku) {
        super("Product with SKU '" + sku + "' already exists");
    }
}