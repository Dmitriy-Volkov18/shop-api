package com.example.shopapi.coupon.services;

import com.example.shopapi.coupon.entities.Coupon;
import com.example.shopapi.coupon.entities.CouponUsage;
import com.example.shopapi.order.entities.CustomerOrder;
import com.example.shopapi.coupon.repositories.CouponUsageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class CouponUsageService {

    private final CouponUsageRepository repository;

    public void recordIfPresent(
            CustomerOrder order
    ) {
        Coupon coupon = order.getCoupon();

        if(coupon == null) {
            return;
        }

        if(repository.findByOrderId(order.getId()).isPresent()) {
            return;
        }

        coupon.registerUsage();

        CouponUsage usage = new CouponUsage();
        usage.setCoupon(coupon);
        usage.setUser(order.getUser());
        usage.setOrder(order);
        usage.setDiscountAmount(order.getCouponDiscount());
        usage.setUsedAt(LocalDateTime.now());

        repository.save(usage);
    }
}