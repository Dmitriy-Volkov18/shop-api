package com.example.shopapi.discounts.categoryDiscounts;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryDiscountRepository
        extends JpaRepository<CategoryDiscount, Long> {

    @Query("""
        select d
        from CategoryDiscount d
        where d.category.id = :categoryId
        and d.status = com.example.shopapi.discounts.enums.DiscountStatus.ACTIVE
        and :now between d.startsAt and d.endsAt
        order by d.priority desc
    """)
    List<CategoryDiscount> findActiveDiscounts(
            @Param("categoryId") Long categoryId,
            @Param("now") LocalDateTime now
    );

    List<CategoryDiscount> findByCategoryId(
            Long categoryId
    );


    Optional<CategoryDiscount> findByIdAndCategoryId(
            Long id,
            Long categoryId
    );
}