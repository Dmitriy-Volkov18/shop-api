package com.example.shopapi.reviews.repositories;

import com.example.shopapi.reviews.entities.ReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewImageRepository
        extends JpaRepository<ReviewImage, Long> {

    List<ReviewImage> findByReviewId(Long reviewId);

    long countByReviewId(Long reviewId);

    Optional<ReviewImage> findById(Long id);
}