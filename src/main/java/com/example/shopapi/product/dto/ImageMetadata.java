package com.example.shopapi.product.dto;

public record ImageMetadata(
        String fileName,
        String contentType,
        Long fileSize,
        String storagePath,
        Integer width,
        Integer height
) {}