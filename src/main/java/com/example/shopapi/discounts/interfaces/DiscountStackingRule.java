package com.example.shopapi.discounts.interfaces;

import com.example.shopapi.discounts.dto.DiscountResult;

import java.util.List;

public interface DiscountStackingRule {

    boolean isApplicable(
            DiscountResult candidate,
            List<DiscountResult> selected
    );

}