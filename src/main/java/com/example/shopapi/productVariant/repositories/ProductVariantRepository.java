package com.example.shopapi.productVariant.repositories;

import com.example.shopapi.productVariant.entities.ProductVariant;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductVariantRepository
        extends JpaRepository<ProductVariant, Long> {

    List<ProductVariant> findByProductId(Long productId);

    Optional<ProductVariant> findByIdAndProductId(
            Long id,
            Long productId
    );

    boolean existsBySku(String sku);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select v
            from ProductVariant v
            where v.id = :id
            """)
    Optional<ProductVariant> findByIdForUpdate(Long id);

    Optional<ProductVariant> findBySku(String sku);
}