package com.example.shopapi.reviews.services;

import com.example.shopapi.common.exception.BadRequestException;
import com.example.shopapi.product.dto.ImageMetadata;
import com.example.shopapi.product.services.ImageFactory;
import com.example.shopapi.product.services.ImageRulesService;
import com.example.shopapi.product.services.ImageUploadService;
import com.example.shopapi.reviews.entities.Review;
import com.example.shopapi.reviews.entities.ReviewImage;
import com.example.shopapi.reviews.repositories.ReviewImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewImageService {

    private static final int MAX_REVIEW_IMAGES = 10;

    private final ReviewService reviewService;
    private final ImageUploadService imageUploadService;
    private final ImageFactory imageFactory;
    private final ImageRulesService imageRulesService;
    private final ReviewImageRepository reviewImageRepository;

    public void addImages(
            Long reviewId,
            List<MultipartFile> files
    ) {
        Review review = reviewService.getById(reviewId);

        imageRulesService.validateImageCount(
                review.getImages().size(),
                files.size(),
                MAX_REVIEW_IMAGES
        );

        for (MultipartFile file : files) {
            ImageMetadata metadata = imageUploadService.process(file);

            ReviewImage image =
                    imageFactory.createReviewImage(
                            review,
                            metadata
                    );

            review.getImages().add(image);
        }

        imageRulesService.normalizeSortOrder(
                review.getImages()
        );
    }

    @Transactional
    public void deleteImage(
            Long imageId
    ) {
        ReviewImage image =
                reviewImageRepository.findById(imageId)
                        .orElseThrow(() ->
                                new BadRequestException("Image not found"));

        Review review = image.getReview();
        review.getImages().remove(image);

        imageRulesService.normalizeSortOrder(
                review.getImages()
        );

    }

    @Transactional(readOnly = true)
    public ReviewImage getImage(
            Long imageId
    ) {
        return reviewImageRepository.findById(imageId)
                .orElseThrow(() ->
                        new BadRequestException("Image not found"));
    }
}