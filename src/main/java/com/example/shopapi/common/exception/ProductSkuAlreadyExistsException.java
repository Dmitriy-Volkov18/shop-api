package com.example.shopapi.common.exception;

public class ProductSkuAlreadyExistsException extends BadRequestException {

    public ProductSkuAlreadyExistsException(String sku) {
        super("Product with SKU '" + sku + "' already exists");
    }
}