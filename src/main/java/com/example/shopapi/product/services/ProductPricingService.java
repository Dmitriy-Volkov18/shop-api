package com.example.shopapi.product.services;

import com.example.shopapi.discounts.DiscountResolver;
import com.example.shopapi.discounts.services.DiscountStackingService;
import com.example.shopapi.productVariant.entities.ProductVariant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ProductPricingService {

    private final DiscountResolver discountResolver;
    private final DiscountStackingService discountStackingService;

    public BigDecimal getBasePrice(
            ProductVariant variant
    ) {

        return variant.getPrice() != null
                ? variant.getPrice()
                : variant.getProduct().getPrice();
    }

    public BigDecimal calculateSubtotal(
            BigDecimal unitPrice,
            int quantity
    ) {

        return unitPrice.multiply(
                BigDecimal.valueOf(quantity)
        );
    }

    public BigDecimal calculateEffectivePrice(
            ProductVariant variant
    ) {

        return discountStackingService.calculate(
                variant,
                getBasePrice(variant)
        );
    }



    // ДЛЯ ОДНОЙ АКТИВНОЙ СКИДКИ

    /*public BigDecimal calculateEffectivePrice(
            ProductVariant variant
    ) {

        BigDecimal basePrice =
                getBasePrice(variant);


        DiscountResult discount =
                discountResolver.resolve(variant)
                        .orElse(null);


        return calculateEffectivePrice(
                basePrice,
                discount
        );
    }


    public BigDecimal calculateEffectivePrice(
            BigDecimal basePrice,
            Optional<DiscountResult> discount
    ) {

        return discount
                .map(value ->
                        calculateEffectivePrice(
                                basePrice,
                                value
                        )
                )
                .orElse(basePrice);
    }


    public BigDecimal calculateEffectivePrice(
            BigDecimal basePrice,
            DiscountResult discount
    ) {

        if(discount == null){
            return basePrice;
        }


        if(discount.isPercent()) {

            return basePrice.subtract(
                    basePrice
                            .multiply(discount.discountValue())
                            .divide(BigDecimal.valueOf(100))
            ).max(BigDecimal.ZERO);

        }


        return basePrice
                .subtract(
                        discount.discountValue()
                )
                .max(BigDecimal.ZERO);
    }*/


}