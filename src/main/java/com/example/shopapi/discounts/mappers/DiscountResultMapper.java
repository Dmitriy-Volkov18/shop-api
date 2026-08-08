package com.example.shopapi.discounts.mappers;

import com.example.shopapi.discounts.dto.ActiveDiscountResponse;
import com.example.shopapi.discounts.dto.DiscountResult;
import com.example.shopapi.discounts.entities.AbstractDiscount;
import org.springframework.stereotype.Component;

@Component
public class DiscountResultMapper {
    public ActiveDiscountResponse toResponse(
            DiscountResult result
    ){
        AbstractDiscount discount =
                result.discount();

        return new ActiveDiscountResponse(
                result.source(),
                discount.getType(),
                discount.getDiscountValue(),
                discount.getDescription(),
                discount.getPriority()
        );
    }
}