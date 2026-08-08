package com.example.shopapi.common.exception;

public class InsufficientStockException
        extends BusinessException {


    public InsufficientStockException() {

        super(
                "Not enough product stock"
        );
    }


    public InsufficientStockException(
            Long variantId
    ) {

        super(
                "Not enough stock for variant: " + variantId
        );
    }
}