package com.example.shopapi.discounts.productDiscounts;

import com.example.shopapi.discounts.dto.DiscountResult;
import com.example.shopapi.productVariant.entities.ProductVariant;
import com.example.shopapi.discounts.enums.DiscountSource;
import com.example.shopapi.discounts.interfaces.DiscountProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductDiscountProvider
        implements DiscountProvider {

    private final ProductDiscountRepository repository;

    @Override
    public Optional<DiscountResult> findDiscount(
            ProductVariant variant
    ) {
        return repository.findActiveDiscounts(
                        variant.getId(),
                        LocalDateTime.now()
                )
                .stream()
                .findFirst()
                .map(this::toResult);
    }

    private DiscountResult toResult(
            ProductDiscount discount
    ) {
        return new DiscountResult(
                discount,
                DiscountSource.PRODUCT
        );
    }
}