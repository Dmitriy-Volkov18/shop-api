package com.example.shopapi.promotion.context;

import com.example.shopapi.card.entities.Cart;
import com.example.shopapi.order.entities.CustomerOrder;
import com.example.shopapi.productVariant.entities.ProductVariant;
import com.example.shopapi.promotion.calculation.CartSubtotalCalculator;
import com.example.shopapi.promotion.engine.PromotionContext;
import com.example.shopapi.user.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class PromotionContextBuilder {


    private final CartSubtotalCalculator subtotalCalculator;


    public PromotionContext build(
            User user,
            Cart cart,
            CustomerOrder order,
            ProductVariant variant
    ) {


        return PromotionContext.builder()

                .user(user)

                .cart(cart)

                .order(order)

                .variant(variant)

                .now(LocalDateTime.now())

                .cartTotal(
                        subtotalCalculator.calculate(cart)
                )

                .build();
    }
}