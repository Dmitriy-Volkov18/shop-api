package com.example.shopapi.coupon.services;

import com.example.shopapi.coupon.dto.CouponResponse;
import com.example.shopapi.coupon.dto.CreateCouponRequest;
import com.example.shopapi.coupon.dto.UpdateCouponRequest;
import com.example.shopapi.coupon.entities.Coupon;
import com.example.shopapi.coupon.CouponMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CouponFacade {

    private final CouponService couponService;
    private final CouponMapper couponMapper;

    @Transactional(readOnly = true)
    public List<CouponResponse> getCoupons() {

        return couponService.getCoupons()
                .stream()
                .map(couponMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CouponResponse getCoupon(
            Long id
    ) {
        return couponMapper.toResponse(couponService.getCoupon(id));
    }

    public CouponResponse create(
            CreateCouponRequest request
    ) {
        Coupon coupon = couponService.create(request);

        CouponResponse response = couponMapper.toResponse(coupon);

        log.info("Coupon is created");

        return response;
    }

    public CouponResponse update(
            Long id,
            UpdateCouponRequest request
    ) {
        Coupon coupon = couponService.getCoupon(id);

        Coupon updated =
                couponService.update(
                        coupon,
                        request
                );

        CouponResponse response = couponMapper.toResponse(updated);

        log.info("Coupon is updated");

        return response;
    }

    public void delete(
            Long id
    ) {
        Coupon coupon = couponService.getCoupon(id);
        couponService.delete(coupon);

        log.info("Coupon is deleted");
    }
}