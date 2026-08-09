package com.example.shopapi.coupon.services;

import com.example.shopapi.coupon.dto.CouponApplicationRequest;
import com.example.shopapi.coupon.dto.CouponApplicationResult;
import com.example.shopapi.coupon.entities.Coupon;
import com.example.shopapi.common.exception.runtimeExceptions.BadRequestException;
import com.example.shopapi.coupon.repositories.CouponUsageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CouponApplicationService {

    private final CouponService couponService;
    private final CouponUsageRepository usageRepository;

    public CouponApplicationResult apply(
            CouponApplicationRequest request
    ) {
        Coupon coupon =
                couponService.getByCode(
                        request.code()
                );

        validate(coupon, request);

        BigDecimal discount =
                coupon.calculateDiscount(
                        request.orderTotal()
                );

        BigDecimal finalTotal =
                request.orderTotal()
                        .subtract(discount);

        return new CouponApplicationResult(
                coupon,
                discount,
                finalTotal
        );
    }

    private void validate(
            Coupon coupon,
            CouponApplicationRequest request
    ) {
        LocalDateTime now =
                LocalDateTime.now();

        if (!coupon.isApplicable(now)) {
            throw new BadRequestException("Coupon is not active");
        }

        if (!coupon.satisfiesMinimumOrder(request.orderTotal())) {
            throw new BadRequestException("Minimum order amount not reached");
        }

        if (!coupon.canBeUsed()) {
            throw new BadRequestException("Coupon usage limit exceeded");
        }

        long userUsages =
                usageRepository
                        .countByCouponIdAndUserId(
                                coupon.getId(),
                                request.user().getId()
                        );

        if (!coupon.canBeUsedByUser(userUsages)) {
            throw new BadRequestException("User coupon limit exceeded");
        }
    }
}