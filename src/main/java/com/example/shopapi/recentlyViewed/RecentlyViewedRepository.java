package com.example.shopapi.recentlyViewed;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecentlyViewedRepository
        extends JpaRepository<RecentlyViewedProduct, Long> {

    Optional<RecentlyViewedProduct> findByUserIdAndProductId(
            Long userId,
            Long productId
    );

    Page<RecentlyViewedProduct> findByUserIdOrderByViewedAtDesc(
            Long userId,
            Pageable pageable
    );

    List<RecentlyViewedProduct> findByUserIdOrderByViewedAtDesc(
            Long userId
    );


    void deleteByUserId(Long userId);

}