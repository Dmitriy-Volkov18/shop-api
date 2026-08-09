package com.example.shopapi.common.exception.notFoundExceptions;

public class WishlistItemNotFoundException extends NotFoundException {

    public WishlistItemNotFoundException(
            Long productId
    ) {
        super("Product with id " + productId + " not found in wishlist");
    }
}