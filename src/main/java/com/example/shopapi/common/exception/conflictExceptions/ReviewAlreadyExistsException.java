package com.example.shopapi.common.exception.conflictExceptions;

public class ReviewAlreadyExistsException extends ConflictException {

    public ReviewAlreadyExistsException(
            Long userId,
            Long productId
    ) {
        super("User %d already reviewed product %d"
                        .formatted(userId, productId)
        );
    }
}