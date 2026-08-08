package com.example.shopapi.common.exception;

public class WishlistItemAlreadyExistsException
        extends RuntimeException {

    public WishlistItemAlreadyExistsException(
            Long productId
    ) {
        super(
                "Product with id " +
                        productId +
                        " is already in wishlist"
        );
    }
}