package com.example.shopapi.wishlist;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface WishlistRepository
        extends JpaRepository<WishlistItem, Long> {

    @EntityGraph(attributePaths = {
            "product",
            "product.category",
            "product.images"
    })
    Page<WishlistItem> findByUserId(
            Long userId,
            Pageable pageable
    );

    List<WishlistItem> findByUserId(Long userId);

    boolean existsByUserIdAndProductId(
            Long userId,
            Long productId
    );

    Optional<WishlistItem> findByUserIdAndProductId(
            Long userId,
            Long productId
    );

    void deleteByUserIdAndProductId(
            Long userId,
            Long productId
    );

    @Query("""
        select w.product.id
        from WishlistItem w
        where w.user.id = :userId
        and w.product.id in :productIds
    """)
    Set<Long> findProductIdsInWishlist(
            @Param("userId") Long userId,
            @Param("productIds") Collection<Long> productIds
    );
}