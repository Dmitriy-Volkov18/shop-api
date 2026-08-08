package com.example.shopapi.promotion.calculation;

import com.example.shopapi.card.entities.Cart;
import com.example.shopapi.card.entities.CartItem;
import com.example.shopapi.product.services.ProductPricingService;
import com.example.shopapi.promotion.engine.PromotionDiscountLine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class CartItemPricingService {

    private final ProductPricingService pricingService;
    private final PromotionDiscountAllocator discountAllocator;


    public CartItemPrice calculate(
            CartItem item,
            CartPriceResult priceResult,
            Cart cart
    ) {

        /*BigDecimal originalUnitPrice =
                pricingService.getBasePrice(
                        item.getVariant()
                );*/

        BigDecimal originalUnitPrice =
                pricingService.calculateEffectivePrice(item.getVariant());


        BigDecimal originalSubtotal =
                pricingService.calculateSubtotal(
                        originalUnitPrice,
                        item.getQuantity()
                );


        // скидки конкретно на этот товар
        BigDecimal lineDiscount =
                calculateLineDiscount(
                        item,
                        priceResult
                );


        // распределённая скидка корзины
        BigDecimal cartDiscount =
                discountAllocator.allocate(
                                cart,
                                priceResult
                        )
                        .getOrDefault(
                                item.getVariant().getId(),
                                BigDecimal.ZERO
                        );


        BigDecimal totalDiscount =
                lineDiscount.add(
                        cartDiscount
                );


        BigDecimal finalSubtotal =
                originalSubtotal
                        .subtract(totalDiscount)
                        .max(BigDecimal.ZERO);


        BigDecimal finalUnitPrice =
                finalSubtotal.divide(
                        BigDecimal.valueOf(
                                item.getQuantity()
                        ),
                        2,
                        RoundingMode.HALF_UP
                );


        return new CartItemPrice(
                originalUnitPrice,
                totalDiscount,
                finalUnitPrice,
                finalSubtotal
        );
    }


    /**
     * Скидки, которые принадлежат конкретному товару
     *
     * Например:
     * Buy 2 Get 1 Free
     */
    private BigDecimal calculateLineDiscount(
            CartItem item,
            CartPriceResult priceResult
    ) {

        return priceResult.appliedPromotions()
                .stream()

                .flatMap(promotion ->
                        promotion.result()
                                .discounts()
                                .stream()
                )

                .filter(discount ->
                        discount.variantId()
                                .equals(
                                        item.getVariant()
                                                .getId()
                                )
                )

                .map(PromotionDiscountLine::discountAmount)

                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );
    }
}