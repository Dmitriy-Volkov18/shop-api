package com.example.shopapi.coupon.repositories;

import com.example.shopapi.coupon.entities.CouponUsage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CouponUsageRepository
        extends JpaRepository<CouponUsage, Long> {

    long countByCouponId(
            Long couponId
    );

    long countByCouponIdAndUserId(
            Long couponId,
            Long userId
    );

    Optional<CouponUsage> findByOrderId(
            Long orderId
    );

    List<CouponUsage> findByCouponId(
            Long couponId
    );

    List<CouponUsage> findByUserId(
            Long userId
    );

    List<CouponUsage> findByCouponIdOrderByUsedAtDesc(Long couponId);

    List<CouponUsage> findByUserIdOrderByUsedAtDesc(Long userId);

    Page<CouponUsage> findAll(Pageable pageable);
}
