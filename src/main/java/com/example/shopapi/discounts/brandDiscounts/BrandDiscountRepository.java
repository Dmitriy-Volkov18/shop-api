package com.example.shopapi.discounts.brandDiscounts;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BrandDiscountRepository
        extends JpaRepository<BrandDiscount, Long> {

    List<BrandDiscount> findByBrandId(
            Long brandId
    );

    Optional<BrandDiscount> findByIdAndBrandId(
            Long id,
            Long brandId
    );

    @Query("""
            select d
            from BrandDiscount d
            where d.brand.id = :brandId
            and d.status = com.example.shopapi.discounts.enums.DiscountStatus.ACTIVE
            and :now between d.startsAt and d.endsAt
            order by d.priority desc,
                     d.applicationOrder asc
            """)
    List<BrandDiscount> findActiveDiscounts(
            Long brandId,
            LocalDateTime now
    );
}