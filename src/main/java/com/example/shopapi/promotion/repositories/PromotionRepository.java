package com.example.shopapi.promotion.repositories;

import com.example.shopapi.promotion.entities.Promotion;
import com.example.shopapi.promotion.enums.PromotionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PromotionRepository
        extends JpaRepository<Promotion, Long> {

    List<Promotion> findByStatus(
            PromotionStatus status
    );

    @Query("""
        select p from Promotion p
        where p.status = :status
        and p.startsAt <= :now
        and p.endsAt >= :now
        """)
    List<Promotion> findActive(
            @Param("status") PromotionStatus status,
            @Param("now") LocalDateTime now
    );

    boolean existsByNameIgnoreCase(
            String name
    );

    @Modifying(clearAutomatically = true)
    @Query("""
update Promotion p
set p.status = :expired
where p.status = :active
and p.endsAt < :now
and p.campaign is null
""")
    void expireActive(
            @Param("now") LocalDateTime now,
            @Param("active") PromotionStatus active,
            @Param("expired") PromotionStatus expired
    );


    @Modifying(clearAutomatically = true)
    @Query("""
update Promotion p
set p.status = :active
where p.status = :scheduled
and p.startsAt <= :now
and p.campaign is null
""")
    void activateScheduled(
            @Param("now") LocalDateTime now,
            @Param("scheduled") PromotionStatus scheduled,
            @Param("active") PromotionStatus active
    );
}
