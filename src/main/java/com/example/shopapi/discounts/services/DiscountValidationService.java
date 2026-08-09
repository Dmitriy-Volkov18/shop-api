package com.example.shopapi.discounts.services;

import com.example.shopapi.discounts.productDiscounts.ProductDiscount;
import com.example.shopapi.productVariant.entities.ProductVariant;
import com.example.shopapi.discounts.enums.DiscountType;
import com.example.shopapi.common.exception.BadRequestException;
import com.example.shopapi.common.validation.ValidationUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class DiscountValidationService {

    public void validateDiscount(
            DiscountType type,
            BigDecimal discountValue,
            LocalDateTime startsAt,
            LocalDateTime endsAt
    ) {
        validateDiscount(
                type,
                discountValue,
                null,
                startsAt,
                endsAt
        );
    }

    public void validateDiscount(
            DiscountType type,
            BigDecimal discountValue,
            BigDecimal basePrice,
            LocalDateTime startsAt,
            LocalDateTime endsAt
    ) {
        validatePeriod(
                startsAt,
                endsAt
        );

        validateValue(
                type,
                discountValue,
                basePrice
        );
    }

    private void validatePeriod(
            LocalDateTime startsAt,
            LocalDateTime endsAt
    ) {
        if (startsAt == null || endsAt == null) {
            throw new BadRequestException(
                    "Discount dates are required"
            );
        }

        if (!startsAt.isBefore(endsAt)) {
            throw new BadRequestException(
                    "Discount start date must be before end date"
            );
        }
    }

    private void validateValue(
            DiscountType type,
            BigDecimal discountValue,
            BigDecimal basePrice
    ) {
        ValidationUtils.requirePositive(
                discountValue,
                "Discount value must be greater than zero"
        );

        ValidationUtils.requirePercentage(discountValue);

        if(type == DiscountType.FIXED_AMOUNT
                && basePrice != null
                && discountValue.compareTo(basePrice) > 0
        ){
            throw new BadRequestException(
                    "Fixed discount cannot exceed product price"
            );
        }
    }

    public void validateNoOverlap(
            ProductVariant variant,
            LocalDateTime startsAt,
            LocalDateTime endsAt,
            Long currentDiscountId
    ) {
        for (ProductDiscount discount : variant.getDiscounts()) {
            if (currentDiscountId != null
                    && currentDiscountId.equals(
                    discount.getId()
            )) {
                continue;
            }

            boolean overlap =
                    startsAt.isBefore(
                            discount.getEndsAt()
                    )
                            &&
                            endsAt.isAfter(
                                    discount.getStartsAt()
                            );

            if (overlap) {
                throw new BadRequestException(
                        "Discount period overlaps with another discount"
                );
            }
        }
    }
}