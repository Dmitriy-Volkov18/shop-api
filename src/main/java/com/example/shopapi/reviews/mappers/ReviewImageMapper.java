package com.example.shopapi.reviews.mappers;

import com.example.shopapi.reviews.dto.ReviewImageResponse;
import com.example.shopapi.reviews.entities.ReviewImage;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReviewImageMapper {

    ReviewImageResponse toResponse(
            ReviewImage image
    );

    List<ReviewImageResponse> toResponse(
            List<ReviewImage> images
    );
}