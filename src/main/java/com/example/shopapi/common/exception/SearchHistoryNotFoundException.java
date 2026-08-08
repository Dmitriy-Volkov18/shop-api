package com.example.shopapi.common.exception;

public class SearchHistoryNotFoundException
        extends NotFoundException {

    public SearchHistoryNotFoundException(
            Long id
    ) {
        super(
                "Search history not found: " + id
        );
    }
}