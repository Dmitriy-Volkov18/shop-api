package com.example.shopapi.discounts.dto;

import com.example.shopapi.discounts.entities.AbstractDiscount;
import com.example.shopapi.discounts.enums.DiscountSource;
import com.example.shopapi.discounts.enums.DiscountType;

import java.math.BigDecimal;

public record DiscountResult(
        AbstractDiscount discount,
        DiscountSource source
) {

    public DiscountType type() {
        return discount.getType();
    }

    public BigDecimal discountValue() {
        return discount.getDiscountValue();
    }

    public Integer priority() {
        return discount.getPriority();
    }

    public String description() {
        return discount.getDescription();
    }

    public boolean isPercent() {
        return discount.getType() == DiscountType.PERCENT;
    }

    public boolean isStackable() {
        return discount.isStackable();
    }

    public boolean isExclusive() {
        return discount.isExclusive();
    }

    public Integer applicationOrder() {
        return discount.getApplicationOrder();
    }
}