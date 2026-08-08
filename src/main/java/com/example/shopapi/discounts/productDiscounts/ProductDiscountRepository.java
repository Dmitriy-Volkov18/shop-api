package com.example.shopapi.discounts.productDiscounts;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductDiscountRepository
        extends JpaRepository<ProductDiscount, Long> {


    List<ProductDiscount> findByVariantId(
            Long variantId
    );


    Optional<ProductDiscount> findByIdAndVariantId(
            Long discountId,
            Long variantId
    );


    @Query("""
        select d
        from ProductDiscount d
        where d.variant.id = :variantId
        and d.status = com.example.shopapi.discounts.enums.DiscountStatus.ACTIVE
        and :now between d.startsAt and d.endsAt
        order by d.priority desc
    """)
    List<ProductDiscount> findActiveDiscounts(
            @Param("variantId") Long variantId,
            @Param("now") LocalDateTime now
    );

}