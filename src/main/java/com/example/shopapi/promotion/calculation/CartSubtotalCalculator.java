package com.example.shopapi.promotion.calculation;

import com.example.shopapi.card.entities.Cart;
import com.example.shopapi.product.services.ProductPricingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CartSubtotalCalculator {


    private final ProductPricingService pricingService;


    public BigDecimal calculate(
            Cart cart
    ) {

        if(cart == null) {
            return BigDecimal.ZERO;
        }


        return cart.getItems()
                .stream()

                .map(item -> {

                    BigDecimal price =
                            pricingService.calculateEffectivePrice(
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
    }
}