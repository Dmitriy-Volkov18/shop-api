package com.example.shopapi.reviews.services;

import com.example.shopapi.common.exception.notFoundExceptions.ReviewNotFoundException;
import com.example.shopapi.reviews.ReviewStatus;
import com.example.shopapi.reviews.entities.Review;
import com.example.shopapi.reviews.repositories.ReviewRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ReviewService {

    private final ReviewRepository repository;

    public ReviewService(ReviewRepository repository) {
        this.repository = repository;
    }

    public Review save(
            Review review
    ) {
        return repository.save(review);
    }

    @Transactional(readOnly = true)
    public Review getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new ReviewNotFoundException(id));
    }

    public void delete(Review review) {
        repository.delete(review);
    }

    @Transactional(readOnly = true)
    public boolean existsByUserAndProduct(Long userId, Long productId) {
        return repository.existsByUserIdAndProductId(
                userId,
                productId
        );
    }

    @Transactional(readOnly = true)
    public Review getByUserAndProduct(Long userId, Long productId) {
        return repository.findByUserIdAndProductId(
                        userId,
                        productId)
                .orElseThrow(() ->
                        new ReviewNotFoundException("Review not found"));
    }

    @Transactional(readOnly = true)
    public Page<Review> getApprovedByProduct(
            Long productId,
            Pageable pageable
    ) {
        return repository.findByProductIdAndStatus(
                productId,
                ReviewStatus.APPROVED,
                pageable
        );
    }

    @Transactional(readOnly = true)
    public Page<Review> getByUser(
            Long userId,
            Pageable pageable
    ) {
        return repository.findByUserId(
                userId,
                pageable
        );
    }
}