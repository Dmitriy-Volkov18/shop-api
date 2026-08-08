package com.example.shopapi.reviews.mappers;

import com.example.shopapi.reviews.dto.CreateReviewRequest;
import com.example.shopapi.reviews.dto.ReviewResponse;
import com.example.shopapi.reviews.dto.UpdateReviewRequest;
import com.example.shopapi.reviews.entities.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(
        componentModel = "spring",
        uses = ReviewImageMapper.class
)
public interface ReviewMapper {

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "username", source = "user.username")
    ReviewResponse toResponse(Review review);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "status", ignore = true)
    Review toEntity(CreateReviewRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "status", ignore = true)
    void updateEntity(
            UpdateReviewRequest request,
            @MappingTarget Review review
    );

}