package com.example.shopapi.common.exception.conflictExceptions;

public class WishlistItemAlreadyExistsException extends ConflictException {

    public WishlistItemAlreadyExistsException(
            Long productId
    ) {
        super("Product with id " + productId + " is already in wishlist");
    }
}