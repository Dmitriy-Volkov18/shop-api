package com.example.shopapi.reviews.services;

import com.example.shopapi.common.exception.runtimeExceptions.PurchaseRequiredException;
import com.example.shopapi.order.services.CustomerOrderQueryService;
import com.example.shopapi.product.services.ProductRatingService;
import com.example.shopapi.reviews.ReviewStatus;
import com.example.shopapi.reviews.dto.CreateReviewRequest;
import com.example.shopapi.reviews.dto.ReviewResponse;
import com.example.shopapi.reviews.dto.UpdateReviewRequest;
import com.example.shopapi.product.services.ProductService;
import com.example.shopapi.product.entities.Product;
import com.example.shopapi.reviews.entities.Review;
import com.example.shopapi.reviews.entities.ReviewImage;
import com.example.shopapi.reviews.mappers.ReviewMapper;
import com.example.shopapi.user.entities.User;
import com.example.shopapi.common.exception.conflictExceptions.ReviewAlreadyExistsException;
import com.example.shopapi.auth.services.AuthorizationService;
import com.example.shopapi.auth.services.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewFacadeService {

    private final ReviewService reviewService;
    private final ProductService productService;
    private final CurrentUserService currentUserService;
    private final AuthorizationService authorizationService;
    private final ReviewMapper reviewMapper;
    private final ProductRatingService productRatingService;
    private final CustomerOrderQueryService customerOrderQueryService;
    private final ReviewImageService reviewImageService;

    public ReviewResponse create(
            Long productId,
            CreateReviewRequest request
    ) {
        User currentUser = currentUserService.getCurrentUserEntity();

        if (!customerOrderQueryService.hasPurchasedProduct(currentUser.getId(), productId)) {
            throw new PurchaseRequiredException(productId);
        }

        if (reviewService.existsByUserAndProduct(currentUser.getId(), productId)) {
            throw new ReviewAlreadyExistsException(
                    currentUser.getId(),
                    productId
            );
        }

        Product product = productService.getProduct(productId);

        Review review = reviewMapper.toEntity(request);
        review.setUser(currentUser);
        review.setProduct(product);
        review.setStatus(ReviewStatus.APPROVED);

        reviewService.save(review);

        refreshRating(product);

        return reviewMapper.toResponse(review);
    }

    public ReviewResponse update(
            Long reviewId,
            UpdateReviewRequest request
    ) {
        Review review = getReview(reviewId);

        authorizationService.requireReviewAccess(review);

        reviewMapper.updateEntity(
                request,
                review
        );

        reviewService.save(review);

        refreshRating(review.getProduct());

        return reviewMapper.toResponse(review);
    }

    public void delete(Long reviewId) {
        Review review = getReview(reviewId);

        authorizationService.requireReviewAccess(review);

        reviewService.delete(review);

        refreshRating(review.getProduct());
    }

    @Transactional(readOnly = true)
    public ReviewResponse getById(Long reviewId) {
        Review review = getReview(reviewId);

        return reviewMapper.toResponse(review);
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> getProductReviews(
            Long productId,
            Pageable pageable
    ) {
        return reviewService
                .getApprovedByProduct(productId, pageable)
                .map(reviewMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> getMyReviews(
            Pageable pageable
    ) {
        Long userId = currentUserService.getCurrentUserId();

        return reviewService
                .getByUser(userId, pageable)
                .map(reviewMapper::toResponse);
    }

    @Transactional
    public void uploadImages(
            Long reviewId,
            List<MultipartFile> files
    ) {
        Review review = getReview(reviewId);

        authorizationService.requireReviewAccess(review);

        reviewImageService.addImages(
                reviewId,
                files
        );
    }

    @Transactional
    public void deleteImage(
            Long imageId
    ) {
        ReviewImage image = reviewImageService.getImage(imageId);

        authorizationService.requireReviewAccess(
                image.getReview()
        );

        reviewImageService.deleteImage(imageId);
    }

    private Review getReview(
            Long id
    ){
        return reviewService.getById(id);
    }

    private void refreshRating(
            Product product
    ) {
        productRatingService.refreshRating(product);
    }
}