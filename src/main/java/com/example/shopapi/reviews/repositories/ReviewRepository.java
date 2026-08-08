package com.example.shopapi.reviews.repositories;

import com.example.shopapi.product.dto.ProductRatingSummary;
import com.example.shopapi.reviews.ReviewStatus;
import com.example.shopapi.reviews.entities.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    boolean existsByUserIdAndProductId(
            Long userId,
            Long productId
    );

    Optional<Review> findByUserIdAndProductId(
            Long userId,
            Long productId
    );

    @EntityGraph(attributePaths = {
            "user",
            "images"
    })
    Page<Review> findByProductIdAndStatus(
            Long productId,
            ReviewStatus status,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {
            "user",
            "images"
    })
    Page<Review> findByUserId(
            Long userId,
            Pageable pageable
    );


    @Query("""

            select new com.example.shopapi.product.dto.ProductRatingSummary(
    count(r),
    avg(r.rating)
)
from Review r
where r.product.id = :productId
""")
    ProductRatingSummary getRatingSummary(Long productId);

}