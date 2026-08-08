package com.example.shopapi.promotion.calculation;

import com.example.shopapi.card.entities.Cart;
import com.example.shopapi.card.entities.CartItem;
import com.example.shopapi.product.services.ProductBasePriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PromotionDiscountAllocator {


    private final ProductBasePriceService basePriceService;

    public Map<Long, BigDecimal> allocate(
            Cart cart,
            CartPriceResult priceResult
    ) {

        Map<Long, BigDecimal> result =
                new HashMap<>();


        BigDecimal cartDiscount =
                priceResult.appliedPromotions()
                        .stream()

                        .map(promotion ->
                                promotion.result()
                                        .discountAmount()
                        )

                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );


        if(cartDiscount.compareTo(BigDecimal.ZERO) <= 0) {
            return result;
        }


        BigDecimal subtotal =
                cart.getItems()
                        .stream()

                        .map(item ->
                                basePriceService.getBasePrice(
                                                item.getVariant()
                                        )
                                        .multiply(
                                                BigDecimal.valueOf(
                                                        item.getQuantity()
                                                )
                                        )
                        )

                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );


        for(CartItem item : cart.getItems()) {


            BigDecimal itemTotal =
                    basePriceService.getBasePrice(
                                    item.getVariant()
                            )
                            .multiply(
                                    BigDecimal.valueOf(
                                            item.getQuantity()
                                    )
                            );


            BigDecimal share =
                    itemTotal
                            .divide(
                                    subtotal,
                                    4,
                                    RoundingMode.HALF_UP
                            );


            BigDecimal itemDiscount =
                    cartDiscount.multiply(
                            share
                    );


            result.put(
                    item.getVariant().getId(),
                    itemDiscount
            );
        }


        return result;
    }
}