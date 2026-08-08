package com.example.shopapi.common.exception;

public class WishlistItemNotFoundException
        extends RuntimeException {

    public WishlistItemNotFoundException(
            Long productId
    ) {
        super(
                "Product with id " +
                        productId +
                        " not found in wishlist"
        );
    }
}