package com.example.shopapi.product.dto;

import java.util.List;

public record ProductListResponsePage(

        List<ProductListResponse> content,

        int page,

        int size,

        long totalElements,

        int totalPages

) {}