package com.example.shopapi.discounts.services;

import com.example.shopapi.discounts.DiscountResolver;
import com.example.shopapi.discounts.dto.DiscountResult;
import com.example.shopapi.productVariant.entities.ProductVariant;
import com.example.shopapi.discounts.interfaces.DiscountCombinationStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DiscountStackingService {

    private final DiscountResolver resolver;
    private final DiscountCombinationStrategy strategy;
    private final DiscountCalculationService calculationService;

    public BigDecimal calculate(
            ProductVariant variant,
            BigDecimal basePrice
    ) {

        List<DiscountResult> discounts =
                strategy.combine(
                        resolver.resolveAll(variant),
                        variant
                );


        List<DiscountResult> ordered =
                discounts.stream()
                        .sorted(
                                Comparator.comparing(
                                        DiscountResult::applicationOrder
                                )
                        )
                        .toList();


        BigDecimal price = basePrice;


        for(DiscountResult discount : ordered){

            price =
                    calculationService.apply(
                            price,
                            discount
                    );
        }


        return price;
    }
}