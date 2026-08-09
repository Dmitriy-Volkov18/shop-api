package com.example.shopapi.discounts.brandDiscounts;

import com.example.shopapi.discounts.dto.DiscountResult;
import com.example.shopapi.product.entities.Product;
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
public class BrandDiscountProvider
        implements DiscountProvider {

    private final BrandDiscountRepository repository;

    @Override
    public Optional<DiscountResult> findDiscount(
            ProductVariant variant
    ) {
        Product product = variant.getProduct();

        if (product.getBrand() == null) {
            return Optional.empty();
        }

        return repository.findActiveDiscounts(
                        product.getBrand().getId(),
                        LocalDateTime.now()
                )
                .stream()
                .findFirst()
                .map(this::toResult);
    }

    private DiscountResult toResult(
            BrandDiscount discount
    ) {
        return new DiscountResult(
                discount,
                DiscountSource.BRAND
        );
    }
}