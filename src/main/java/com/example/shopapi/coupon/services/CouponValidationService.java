package com.example.shopapi.coupon.services;

import com.example.shopapi.coupon.entities.Coupon;
import com.example.shopapi.common.exception.BadRequestException;
import com.example.shopapi.coupon.repositories.CouponRepository;
import com.example.shopapi.common.validation.ValidationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CouponValidationService {

    private final CouponRepository repository;

    public void validateForCreate(
            Coupon coupon
    ) {

        validateFields(coupon);

        validateUniqueCode(
                coupon.getCode()
        );
    }

    public void validateForUpdate(
            Coupon coupon,
            String previousCode
    ) {

        validateFields(coupon);

        if (!previousCode.equalsIgnoreCase(coupon.getCode())) {

            validateUniqueCode(
                    coupon.getCode()
            );
        }
    }

    public void validateFields(
            Coupon coupon
    ) {

        validateDates(coupon);

        validateDiscount(coupon);

        validateLimits(coupon);

    }

    private void validateUniqueCode(
            String code
    ) {

        if (repository.existsByCodeIgnoreCase(code)) {

            throw new BadRequestException(
                    "Coupon code already exists"
            );
        }
    }

    private void validateDates(
            Coupon coupon
    ) {

        ValidationUtils.requireDateRange(
                coupon.getStartsAt(),
                coupon.getEndsAt(),
                "Coupon start date must be before end date"
        );
    }

    private void validateDiscount(
            Coupon coupon
    ) {

        if (coupon.isPercent()) {

            ValidationUtils.requirePercentage(
                    coupon.getDiscountValue()
            );

            return;
        }

        ValidationUtils.requirePositive(
                coupon.getDiscountValue(),
                "Discount value must be greater than zero"
        );
    }

    private void validateLimits(
            Coupon coupon
    ) {

        if (coupon.getUsageLimit() != null) {

            ValidationUtils.requirePositive(
                    coupon.getUsageLimit(),
                    "Usage limit must be positive"
            );
        }

        if (coupon.getPerUserLimit() != null) {

            ValidationUtils.requirePositive(
                    coupon.getPerUserLimit(),
                    "Per-user limit must be positive"
            );
        }

        ValidationUtils.requireNonNegative(
                coupon.getMinimumOrderAmount(),
                "Minimum order amount cannot be negative"
        );

        if (coupon.getMaximumDiscountAmount() != null) {

            ValidationUtils.requirePositive(
                    coupon.getMaximumDiscountAmount(),
                    "Maximum discount must be positive"
            );
        }
    }

}