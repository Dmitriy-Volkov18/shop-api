package com.example.shopapi.product.services;

import com.example.shopapi.product.dto.ProductRatingSummary;
import com.example.shopapi.product.entities.Product;
import com.example.shopapi.reviews.repositories.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductRatingService {

    private final ReviewRepository reviewRepository;

    public void refreshRating(Product product) {

        ProductRatingSummary summary =
                reviewRepository.getRatingSummary(
                        product.getId()
                );

        if (summary.reviewCount() == 0) {
            product.clearRating();

            return;
        }

        product.updateRating(
                Math.toIntExact(summary.reviewCount()),
                BigDecimal.valueOf(summary.averageRating())
                        .setScale(2, RoundingMode.HALF_UP)
        );
    }
}