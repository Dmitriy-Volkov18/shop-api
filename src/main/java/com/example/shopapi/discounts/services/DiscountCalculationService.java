package com.example.shopapi.discounts.services;

import com.example.shopapi.discounts.dto.DiscountResult;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class DiscountCalculationService {

    public BigDecimal apply(
            BigDecimal price,
            DiscountResult discount
    ) {
        if (discount.isPercent()) {
            return price.subtract(
                    price.multiply(discount.discountValue())
                            .divide(
                                    BigDecimal.valueOf(100),
                                    2,
                                    RoundingMode.HALF_UP
                            )
            ).max(BigDecimal.ZERO);
        }

        return price.subtract(discount.discountValue()).max(BigDecimal.ZERO);
    }

}