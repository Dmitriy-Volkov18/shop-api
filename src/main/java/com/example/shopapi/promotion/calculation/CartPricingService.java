package com.example.shopapi.promotion.calculation;

import com.example.shopapi.card.entities.Cart;
import com.example.shopapi.product.services.ProductPricingService;
import com.example.shopapi.promotion.engine.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartPricingService {

    private final ProductPricingService productPricingService;
    private final PromotionEngine promotionEngine;

    public CartPriceResult calculate(
            Cart cart,
            PromotionContext context
    ) {
        BigDecimal subtotal =
                cart.getItems()
                        .stream()

                        .map(item -> {

                            BigDecimal price =
                                    productPricingService.calculateEffectivePrice(
                                            item.getVariant()
                                    );


                            return price.multiply(
                                    BigDecimal.valueOf(
                                            item.getQuantity()
                                    )
                            );

                        })

                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );


        List<AppliedPromotion> promotions =
                promotionEngine.apply(
                        context
                );


        BigDecimal promotionDiscount =
                promotions.stream()

                        .map(applied ->
                                applied.result()
                                        .discountAmount()
                        )

                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );


        BigDecimal itemDiscount =
                promotions.stream()

                        .flatMap(applied ->
                                applied.result()
                                        .discounts()
                                        .stream()
                        )

                        .map(PromotionDiscountLine::discountAmount)

                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );


        BigDecimal totalDiscount =
                promotionDiscount.add(
                        itemDiscount
                );


        BigDecimal total =
                subtotal
                        .subtract(totalDiscount)
                        .max(BigDecimal.ZERO);


        return new CartPriceResult(
                subtotal,
                totalDiscount,
                total,
                promotions
        );
    }

}